package org.zb.plugin.restdoc.utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.restdoc.constant.BaseTypeConstant;
import org.zb.plugin.restdoc.constant.CommonConstant;
import org.zb.plugin.restdoc.constant.SpringConstant;
import org.zb.plugin.restdoc.definition.ConvertFieldType;
import org.zb.plugin.restdoc.definition.MethodDefinition;

import java.util.*;

/**
 * @author Tony Yan
 */
public abstract class MyPsiSupport {

    /**
     * 通过AnActionEvent 对象获得 PsiFile 对象
     *
     * @param event
     * @return
     */
    public static PsiFile getPsiFileByEvent(AnActionEvent event) {
        return event.getData(LangDataKeys.PSI_FILE);
    }

    /**
     * 通过AnActionEvent 对象获得 PsiJavaFile 对象 如果当前Event的File不是JavaFile类型则返回空
     *
     * @param event
     * @return
     */
    public static PsiJavaFile getPsiJavaFile(AnActionEvent event) {
        PsiFile file = getPsiFileByEvent(event);
        if (file instanceof PsiJavaFile) {
            return (PsiJavaFile) file;
        }
        return null;
    }

    public static Set<PsiMethod> getAllRequestPsiMethod(List<PsiClass> psiClassList) {
        Set<PsiMethod> targetMethod = new HashSet<>();
        if (psiClassList != null) {
            List<PsiMethod> postMethods = MyPsiSupport.getPsiMethods(psiClassList, SpringConstant.ANNOTATION_POSTMAPPING);
            List<PsiMethod> requestMethods = MyPsiSupport.getPsiMethods(psiClassList, SpringConstant.ANNOTATION_REQUESTMAPPING);
            List<PsiMethod> getMethods = MyPsiSupport.getPsiMethods(psiClassList, SpringConstant.ANNOTATION_GETMAPPING);
            targetMethod.addAll(postMethods);
            targetMethod.addAll(getMethods);
            targetMethod.addAll(requestMethods);
        }
        return targetMethod;

    }

    /**
     * 通过AnActionEvent对象获得 PsiClass对象数组 如果当前Event的File不是JavaFile类型则返回空
     *
     * @param event
     * @return
     */
    public static PsiClass[] getPsiClass(AnActionEvent event) {
        PsiJavaFile javaFile = getPsiJavaFile(event);
        if (javaFile != null) {
            return javaFile.getClasses();
        }
        return null;
    }


    /**
     * 获得当前类的方法
     *
     * @param psiClass
     * @return
     */
    public static PsiMethod[] getPsiMethods(PsiClass psiClass) {
        return psiClass.getMethods();
    }


    /**
     * 获得当前类的标识某annotation的方法
     *
     * @param psiClass
     * @param annotation
     * @return
     */
    public static List<PsiMethod> getPsiMethods(PsiClass psiClass, String annotation) {

        PsiMethod[] methods = psiClass.getMethods();
        List<PsiMethod> psiMethods = new ArrayList<>();
        for (PsiMethod method : methods) {
            if (getPsiAnnotation(method, annotation) != null) {
                psiMethods.add(method);
            }
        }
        return psiMethods;
    }

    /**
     * 获得当前类的标识某annotation的方法
     *
     * @param psiClasses
     * @param annotation
     * @return
     */
    public static List<PsiMethod> getPsiMethods(List<PsiClass> psiClasses, String annotation) {

        List<PsiMethod> psiMethods = new ArrayList<>();
        for (PsiClass psiClass : psiClasses) {
            psiMethods.addAll(getPsiMethods(psiClass, annotation));
        }
        return psiMethods;
    }

    /**
     * 获得第一个拥有该annotation 全限定名称的 psiClass对象
     *
     * @param javaFile
     * @param annotation
     * @return
     */
    public static PsiClass getFirstPsiClass(PsiJavaFile javaFile, String annotation) {
        PsiClass[] psiClasses = javaFile.getClasses();
        for (PsiClass psiClass : psiClasses) {
            if (getPsiAnnotation(psiClass, annotation) != null) {
                return psiClass;
            }
        }
        return null;
    }

    /**
     * 获得所有拥有该annotation 全限定名称的 psiClass对象
     *
     * @param javaFile
     * @param annotation
     * @return
     */
    public static List<PsiClass> getPsiClasses(PsiJavaFile javaFile, String annotation) {
        PsiClass[] psiClasses = javaFile.getClasses();
        List<PsiClass> psiClassList = new ArrayList<>();
        for (PsiClass psiClass : psiClasses) {
            if (getPsiAnnotation(psiClass, annotation) != null) {
                psiClassList.add(psiClass);
            }
        }
        return psiClassList;
    }

    /**
     * 通过annotation 的全限定名称 获得PsiClass 的某个PsiAnnotation 对象
     *
     * @param psiClass
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiClass psiClass, String annotation) {
        PsiAnnotation[] psiAnnotations = psiClass.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }


    /**
     * 通过annotation 的全限定名称 获得psiField 的某个PsiAnnotation 对象
     *
     * @param psiField
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiField psiField, String annotation) {
        PsiAnnotation[] psiAnnotations = psiField.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }

    /**
     * 通过annotation 的全限定名称 获得PsiMethod 的某个PsiAnnotation 对象
     *
     * @param psiMethod
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiMethod psiMethod, String annotation) {
        PsiAnnotation[] psiAnnotations = psiMethod.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }

    /**
     * 通过annotation 的全限定名称 获得PsiParameter 的某个PsiAnnotation 对象
     *
     * @param psiParameter
     * @param annotation
     * @return
     */
    public static PsiAnnotation getPsiAnnotation(PsiParameter psiParameter, String annotation) {
        PsiAnnotation[] psiAnnotations = psiParameter.getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (psiAnnotation.getQualifiedName().equals(annotation)) {
                return psiAnnotation;
            }
        }
        return null;
    }

    /**
     * 获得annotation 某属性的值
     *
     * @param psiAnnotation
     * @param attrName
     * @return
     */
    public static String getPsiAnnotationValueByAttr(PsiAnnotation psiAnnotation, String attrName) {

        if (psiAnnotation == null) {
            return null;
        }
        ToolUtil.LOGGER.error("注解----2  {},++++ {}", psiAnnotation.getQualifiedName(),psiAnnotation.getText());
        PsiAnnotationMemberValue attributeValue = psiAnnotation.findAttributeValue(attrName);

        if (attributeValue != null && StringUtils.isNotBlank(attributeValue.getText())) {
            String txt = attributeValue.getText();
            if(txt.equals("{}")){
                return null;
            }
            if (txt.contains("\"")) {
                txt = txt.substring(1, txt.length() - 1);
            }
            ToolUtil.LOGGER.info("注解属性名 ：{},注解属性值 ：{}", attrName,attributeValue.getText());
            return txt.trim();
        }else {
            ToolUtil.LOGGER.error("注解----22   {}", attrName);
        }
        return null;
    }


    /**
     * 获得当前选中的文本
     *
     * @param anActionEvent
     * @return
     */
    public static String getSelectedText(AnActionEvent anActionEvent) {
        final Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        String selectedText = editor.getSelectionModel().getSelectedText();
        return selectedText;
    }

    /**
     * 通过方法名获得psiMethod对象
     *
     * @param psiClass
     * @param methodName
     * @return
     */
    public static PsiMethod findPsiMethod(PsiClass psiClass, String methodName) {
        PsiMethod[] methods = psiClass.getAllMethods();
        for (PsiMethod psiMethod : methods) {
            if (psiMethod.getName().equals(methodName)) {
                return psiMethod;
            }
        }
        return null;
    }

    /**
     * 通过类名获得 相应的PsiClass对象
     *
     * @param qualifiedName
     * @param project
     * @return
     */
    public static PsiClass getPsiClass(String qualifiedName, Project project) {
        PsiClass psiClass = JavaPsiFacade.getInstance(project)
                .findClass(qualifiedName, GlobalSearchScope.allScope(project));
        return psiClass;
    }


    /**
     * 通过PsiType对象获得 相应的PsiClass对象
     *
     * @param psiType
     * @return 基本类型返回null， 如 int ...
     */
    public static PsiClass getPsiClass(PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        return psiClass;
    }

    /**
     * 解析泛型对应表
     *
     * @return
     */
    public static Map<PsiTypeParameter, PsiType> resolveGenericsMap(PsiType psiType) {
        PsiClassType psiClassType = (PsiClassType) psiType;
        Map<PsiTypeParameter, PsiType> map = psiClassType.resolveGenerics().getSubstitutor().getSubstitutionMap();
        return map;
    }

    /**
     * 获得泛型PsiType
     *
     * @param psiType
     * @param index
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, Integer index) {
        Map<PsiTypeParameter, PsiType> map = resolveGenericsMap(psiType);
        PsiClassType psiClassType = (PsiClassType) psiType;
        if (psiClassType.resolve().getTypeParameters() == null || psiClassType.resolve().getTypeParameters().length - 1 < index) {
            return null;
        }
        PsiType paramType = map.get(psiClassType.resolve().getTypeParameters()[index]);
        return paramType;
    }

    /**
     * 获得字段的泛型信息
     *
     * @param psiType
     * @param psiField
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, PsiField psiField) {
        String typeName = psiField.getType().getCanonicalText();
        return getGenericsType(psiType, typeName);
    }

    /**
     * 获得字段的泛型信息
     *
     * @param psiType
     * @param psiParameter
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, PsiParameter psiParameter) {
        String typeName = psiParameter.getType().getCanonicalText();
        return getGenericsType(psiType, typeName);
    }

    /**
     * 通过泛型名称获得泛型PsiType
     *
     * @param psiType
     * @param genericsName
     * @return
     */
    public static PsiType getGenericsType(PsiType psiType, String genericsName) {
        Map<PsiTypeParameter, PsiType> map = resolveGenericsMap(psiType);
        for (Map.Entry<PsiTypeParameter, PsiType> entry : map.entrySet()) {
            if (entry.getKey().getText().equals(genericsName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 获得泛型数量
     * 基本类型 void 不支持强转，对象可以
     * 泛型数量 ----    2 ++ Map<Integer, Map<Integer, String>>
     * ----    1  List<List<List<Integer>>>
     *
     * @param psiType
     * @return
     */
    public static Integer getCenericsTypeCount(PsiType psiType) {
        if (BaseTypeConstant.isNormalType(psiType)) {
            return 0;
        }
        PsiClassType psiClassType = (PsiClassType) psiType;
        if (psiClassType.resolve().getTypeParameters() == null) {
            return 0;
        }
        return psiClassType.resolve().getTypeParameters().length;
    }

    public static MethodDefinition getMethodDefinition(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return null;
        }
        MethodDefinition md = new MethodDefinition();
        md.setName(psiMethod.getName());
        PsiType rtnType = psiMethod.getReturnType();
        ConvertFieldType convertFieldType = BaseTypeConstant.getTypeName(rtnType);
        md.setRtnTypeEnum(convertFieldType.getTransType());
        md.setRtnType(convertFieldType.getTypeName());
        md.setRtnVal(convertFieldType.getValue());
        String text = JavaDocUtils.getText(psiMethod.getDocComment());
        if (StringUtils.isNotBlank(text) && text.contains("\n")) {
            md.setTitle(text.substring(0, text.indexOf("\n")));
            md.setDesc(text.substring(text.indexOf("\n") + 1));
        } else {
            md.setTitle(text);
        }
        if(StringUtils.isBlank(text)){
            PsiAnnotation methodSwaggerApi = MyPsiSupport.getPsiAnnotation(psiMethod, CommonConstant.SWAGGER_API);
            if(methodSwaggerApi!=null){
                text = MyPsiSupport.getPsiAnnotationValueByAttr(methodSwaggerApi, "value");
                md.setTitle(text);
            }
        }
        List<MethodDefinition.MethodTag> tagList = new ArrayList<>();
        if (psiMethod.getDocComment() != null) {
            PsiDocTag[] docTagList = psiMethod.getDocComment().getTags();
            for (PsiDocTag docTag : docTagList) {
                String val = JavaDocUtils.convertDesc(docTag.getValueElement() != null ? docTag.getValueElement().getText() : "");
                if (StringUtils.equals(docTag.getName(), "return")) {
                    md.setRtnDesc(val);
                } else {
                    tagList.add(MethodDefinition.bulidMethodTag(docTag.getName(), val, JavaDocUtils.getTagText(docTag.getDataElements())));
                }
            }
        }
        md.setTagList(tagList);

        List<MethodDefinition.Param> paramList = new ArrayList<>();
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            List<String> ann = new ArrayList<>();
            for (PsiAnnotation psiAnnotation : psiParameter.getAnnotations()) {
                ann.add(psiAnnotation.getQualifiedName());
            }
            ConvertFieldType paramConvert = BaseTypeConstant.getTypeName(psiParameter.getType());
            paramList.add(MethodDefinition.buildParam(psiParameter.getName(),
                    psiParameter.getType().getPresentableText(), paramConvert, ann));
        }
        md.setParamList(paramList);
        return md;
    }


}
