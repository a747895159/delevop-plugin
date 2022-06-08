package org.zb.plugin.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.restdoc.constant.BaseTypeConstant;
import org.zb.plugin.restdoc.constant.CommonConstant;
import org.zb.plugin.restdoc.definition.FieldDefinition;
import org.zb.plugin.restdoc.definition.KV;
import org.zb.plugin.restdoc.delegate.DelegateFactory;
import org.zb.plugin.restdoc.delegate.GeneratorDelegate;
import org.zb.plugin.restdoc.parser.ObjectParser;
import org.zb.plugin.restdoc.parser.translator.TypeTranslator;
import org.zb.plugin.restdoc.utils.ContentShowUtil;
import org.zb.plugin.restdoc.utils.MyPsiSupport;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * model转换Json
 *
 * @author :  ZhouBin
 * @date :  2019-11-04
 */
public class Bean2JsonAction extends AnAction {

    public static boolean isShowComment = false;

    private GeneratorDelegate delegate;

    private GeneratorDelegate getDelegate() {
        if (delegate == null) {
            delegate = DelegateFactory.getGeneratorDelegate(this.getClass());
        }
        return delegate;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        Project project = editor.getProject();
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
        try {
            KV kv = getFields(selectedClass);
            String json = kv.toPrettyJson();
            String docDesc = genFieldDesc(selectedClass);
            ToolUtil.writeClipboard(docDesc +"\n\n### 示例\n"+json,project,selectedClass.getName(),"JSON");
        } catch (Exception ex) {
            ToolUtil.LOGGER.error("Convert to JSON failed.\n",ex);
            ToolUtil.notifyMsg("Convert to JSON failed.\n",NotificationType.ERROR,project);
        }
    }

    private String genFieldDesc(PsiClass selectedClass){
        PsiMethod[] allMethods = selectedClass.getAllMethods();
        PsiMethod method = null;
        for (PsiMethod psiMethod : allMethods) {
            PsiType returnType = psiMethod.getReturnType();
            if(returnType!=null ) {
                PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(returnType);
                if (selectedClass == psiClass){
                    method = psiMethod;
                    break;
                }
            }
        }
        if(method!=null){
            ObjectParser objectParser =new ObjectParser(method.getReturnType(),method.getProject(),0);
            objectParser.parseDefinition();
            List<FieldDefinition> fieldDefinitions = objectParser.getFieldDefinitions();
            if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder("### 参数\n \n");
            sb.append("|参数名|类型|说明|\n");
            sb.append("|:----    |:---|:----- |-----   |\n");
            sb.append(ContentShowUtil.fieldDefinitionTableBody(fieldDefinitions,false));
            return sb.toString();
        }
        return null;
    }


    public static KV getFields(PsiClass psiClass) {
        KV<String,Object> kv = KV.create();
        KV<String,Object> commentKV = KV.create();

        if (psiClass != null) {
            for (PsiField field : psiClass.getAllFields()) {
                PsiType type = field.getType();
                String name = field.getName();
                if (field.getModifierList() == null || field.getModifierList().hasExplicitModifier(PsiModifier.FINAL)) {
                    continue;
                }
                //doc comment
                if (field.getDocComment() != null && field.getDocComment().getText() != null) {
                    String filedDesc = ToolUtil.clearDesc(field.getDocComment().getText());
                    if(StringUtils.isBlank(filedDesc)){
                        PsiAnnotation swaggerAnnotation = MyPsiSupport.getPsiAnnotation(field, CommonConstant.SWAGGER_DOC);
                        if(swaggerAnnotation!=null){
                            filedDesc = MyPsiSupport.getPsiAnnotationValueByAttr(swaggerAnnotation, "value");
                        }
                    }
                    commentKV.set(name, filedDesc==null?"":filedDesc);
                }
                //primitive Type
                if (type instanceof PsiPrimitiveType) {
                    kv.set(name, PsiTypesUtil.getDefaultValue(type));
                    //reference Type
                } else {
                    //normal Type
                    if (BaseTypeConstant.isNormalType(type)) {
                        kv.set(name, BaseTypeConstant.TYPE_VALUE.get(type.getPresentableText()));
                        //array type
                    } else if (type instanceof PsiArrayType) {
                        PsiType deepType = type.getDeepComponentType();
                        ArrayList<Object> list = new ArrayList<>();
                        String deepTypeName = deepType.getPresentableText();
                        if(BaseTypeConstant.isEnum(deepType)){
                            list.add(BaseTypeConstant.C_ENUM);
                        }else if (deepType instanceof PsiPrimitiveType) {
                            list.add(PsiTypesUtil.getDefaultValue(deepType));
                        } else if (BaseTypeConstant.isNormalType(deepType)) {
                            list.add(BaseTypeConstant.TYPE_VALUE.get(deepTypeName));
                        } else {
                            list.add(getFields(PsiUtil.resolveClassInType(deepType)));
                        }
                        kv.set(name, list);
                        //list type
                    } else if (TypeTranslator.collectionTypeList.contains(MyPsiSupport.getPsiClass(type).getQualifiedName())) {
                        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                        PsiClass iterableClass = MyPsiSupport.getPsiClass(iterableType);
                        ArrayList<Object> list = new ArrayList<>();
                        assert iterableClass != null;
                        String classTypeName = iterableClass.getName();
                        if(BaseTypeConstant.isEnum(iterableType)){
                            list.add(BaseTypeConstant.C_ENUM);
                        }else if (BaseTypeConstant.isNormalType(classTypeName)) {
                            list.add(BaseTypeConstant.TYPE_VALUE.get(classTypeName));
                        } else {
                            list.add(getFields(iterableClass));
                        }
                        kv.set(name, list);
                        //enum
                    } else if (BaseTypeConstant.isEnum(type)) {
                        kv.set(name, BaseTypeConstant.C_ENUM);
                    //class type
                    } else {
                        kv.set(name, getFields(PsiUtil.resolveClassInType(type)));
                    }
                }
            }

            if (isShowComment && commentKV.size() > 0) {
                kv.set("@属性描述", commentKV);
            }
        }

        return kv;
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        this.getDelegate().doUpdate(e);
    }

}
