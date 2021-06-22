package org.zb.plugin.restdoc.parser;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import org.zb.plugin.restdoc.constant.BaseTypeConstant;
import org.zb.plugin.restdoc.constant.CommonConstant;
import org.zb.plugin.restdoc.definition.ConvertFieldType;
import org.zb.plugin.restdoc.definition.FieldDefinition;
import org.zb.plugin.restdoc.parser.translator.TypeTranslator;
import org.zb.plugin.restdoc.utils.Convertor;
import org.zb.plugin.restdoc.utils.JavaDocUtils;
import org.zb.plugin.restdoc.utils.MyPsiSupport;

import java.util.ArrayList;
import java.util.List;

public class ObjectParser extends Parser {

    private PsiClass psiClass;
    private Integer layer;
    private PsiType psiType;
    private Project project;
    private List<FieldDefinition> fieldDefinitions = new ArrayList<>();


    public ObjectParser(PsiType psiType, Project project, Integer layer) {
        this.psiType = psiType;
        this.project = project;
        this.psiClass = MyPsiSupport.getPsiClass(psiType);
        this.layer = layer;
    }

    @Override
    public void parseDefinition() {
        ConvertFieldType convertFieldType = BaseTypeConstant.getTypeName(psiType);
        if (BaseTypeConstant.C_VOID.equals(convertFieldType.getTypeName())) {
            return;
        }
        //  请求参数 只解析对象
        List<PsiField> psiFieldList = new ArrayList<>();
        switch (convertFieldType.getTransType()) {

            case LIST:
                PsiType genericsType = MyPsiSupport.getGenericsType(psiType, 0);
                if (genericsType != null) {
                    PsiClass genericsClass = MyPsiSupport.getPsiClass(genericsType);
                    psiFieldList = getAvailablePsiField(genericsClass, genericsClass.getAllFields());
                }

                break;

            case ARRAY:
                PsiType deepType = psiType.getDeepComponentType();
                if(!(BaseTypeConstant.isNormalType(deepType) || BaseTypeConstant.isEnum(deepType))){
                    PsiClass genericsClass = MyPsiSupport.getPsiClass(deepType);
                    psiFieldList = getAvailablePsiField(genericsClass, genericsClass.getAllFields());
                }
                break;

            case OTHER_OBJECT:
                psiFieldList = this.getAvailablePsiField(this.psiClass, psiClass.getAllFields());
                break;
            default:
                break;
        }


        if (psiFieldList.isEmpty()) {
            return;
        }
        doParse(psiFieldList);
    }

    /**
     * 提前解析后的内容
     *
     * @return
     */
    public List<FieldDefinition> getFieldDefinitions() {
        return this.fieldDefinitions;
    }

    /**
     * 将没有Getter方法的字段过滤
     *
     * @return
     */
    public List<PsiField> getAvailablePsiField(PsiClass psiClass, PsiField[] psiFields) {
        List<PsiField> psiFieldList = new ArrayList<>();
        for (PsiField psiField : psiFields) {
            if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldGetterName(psiField.getName())) != null) {
                psiFieldList.add(psiField);
            }
            if (MyPsiSupport.findPsiMethod(psiClass, Convertor.getFieldBoolGetterName(psiField.getName())) != null) {
                psiFieldList.add(psiField);
            }
        }
        return psiFieldList;
    }

    /**
     * 解析
     *
     * @param psiFields
     */
    public void doParse(List<PsiField> psiFields) {
        for (PsiField psiField : psiFields) {
            FieldDefinition definition = parseSingleFieldDefinition(psiField);
            this.fieldDefinitions.add(definition);
        }
    }


    /**
     * 单个字段注释递归解析
     *
     * @param psiField
     * @return
     */
    public FieldDefinition parseSingleFieldDefinition(PsiField psiField) {


        FieldDefinition definition = new FieldDefinition();
        String dec = JavaDocUtils.getText(psiField.getDocComment());
        String name = psiField.getName();
        boolean require = MyPsiSupport.getPsiAnnotation(psiField, CommonConstant.CONSTRAINTS_NOTNULL) != null
                || MyPsiSupport.getPsiAnnotation(psiField, CommonConstant.CONSTRAINTS_NOTBLANK) != null
                || MyPsiSupport.getPsiAnnotation(psiField, CommonConstant.CONSTRAINTS_NOTMPTY) != null
                || MyPsiSupport.getPsiAnnotation(psiField, CommonConstant.CONSTRAINTS_NOTBLANK2) != null;
        definition.setLayer(layer);
        definition.setName(name);
        definition.setDesc(dec);
        definition.setRequire(require);
        PsiType fieldType = getRealType(this.psiType, psiField);
        PsiClass fieldClass = MyPsiSupport.getPsiClass(fieldType);

        ConvertFieldType convertFieldType = BaseTypeConstant.getTypeName(fieldType);
        definition.setType(convertFieldType.getTypeName());
        definition.setVal(convertFieldType.getValue());
        PsiType deepType = null;
        PsiClass deepClass = null;
        if (BaseTypeConstant.isNormalType(fieldType)) {

        } else if (fieldType instanceof PsiArrayType) {
            deepType = fieldType.getDeepComponentType();
            if (!BaseTypeConstant.isEnum(deepType)) {
                deepClass = MyPsiSupport.getPsiClass(deepType);
            }
        } else if (TypeTranslator.collectionTypeList.contains(fieldClass.getQualifiedName())) {
            PsiType genericsType = PsiUtil.extractIterableTypeParameter(psiField.getType(), true);
            if (genericsType == null) {
                genericsType = PsiUtil.extractIterableTypeParameter(fieldType, true);
            }
            PsiType listGenericsType = MyPsiSupport.getGenericsType(this.psiType, genericsType.getCanonicalText());
            if (listGenericsType != null) {
                genericsType = listGenericsType;
            }
            if (!BaseTypeConstant.isEnum(genericsType)) {
                deepClass = MyPsiSupport.getPsiClass(genericsType);
                deepType = genericsType;
            }
        } else if (BaseTypeConstant.isEnum(fieldType)) {


        } else if (definition.getType().equals(BaseTypeConstant.C_OBJECT)) {
            deepClass = MyPsiSupport.getPsiClass(fieldType);
            deepType = fieldType;

        }

        if (deepClass != null) {
            ObjectParser objectParser = new ObjectParser(deepType, this.project, layer + 1);
            objectParser.parseDefinition();
            definition.setSubFieldDefinitions(objectParser.getFieldDefinitions());
        }
        return definition;
    }


}
