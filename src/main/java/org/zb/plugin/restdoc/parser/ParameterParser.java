package org.zb.plugin.restdoc.parser;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import org.zb.plugin.restdoc.constant.BaseTypeConstant;
import org.zb.plugin.restdoc.constant.CommonConstant;
import org.zb.plugin.restdoc.definition.ConvertFieldType;
import org.zb.plugin.restdoc.definition.FieldDefinition;
import org.zb.plugin.restdoc.parser.translator.TypeTranslator;
import org.zb.plugin.restdoc.utils.JavaDocUtils;
import org.zb.plugin.restdoc.utils.MyPsiSupport;

import java.util.ArrayList;
import java.util.List;

public class ParameterParser extends Parser {

    private PsiParameter[] psiParameters;
    private PsiMethod psiMethod;
    private List<FieldDefinition> fieldDefinitions = new ArrayList<>();

    public ParameterParser(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        this.psiParameters = psiMethod.getParameterList().getParameters();
    }

    public List<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions;
    }

    @Override
    public void parseDefinition() {

        if (psiParameters == null || psiParameters.length == 0) {
            return;
        }
        /**
         * 此处判断参数是否含有非基本类型对象，若含有对象，解析第一个对象
         */
        PsiParameter firstParameter = null;
        for (PsiParameter psiParameter : this.psiParameters) {
            PsiType psiType = psiParameter.getType();
            PsiClass psiClass = MyPsiSupport.getPsiClass(psiType);
            if(psiClass != null){
                String transName = BaseTypeConstant.getTypeName(psiType).getTypeName();
                // 常规对象
                if (transName.equals(TypeTranslator.TYPE_OBJ) && !CommonConstant.IGNORE_MODEL.contains(psiClass.getQualifiedName())) {
                    firstParameter = psiParameter;
                    break;
                }
            }
        }
        if (firstParameter != null) {
            ObjectParser objectParser = new ObjectParser(firstParameter.getType(), firstParameter.getProject(), 0);
            objectParser.parseDefinition();
            this.fieldDefinitions = objectParser.getFieldDefinitions();
            return;
        }
        doParse();
    }

    public void doParse() {
        if (psiParameters == null || psiParameters.length == 0) {
            return;
        }
        for (PsiParameter psiParameter : this.psiParameters) {
            FieldDefinition definition = this.parseSingleParameterDefinition(psiParameter);
            if (definition != null) {
                this.fieldDefinitions.add(definition);
            }
        }
    }


    public FieldDefinition parseSingleParameterDefinition(PsiParameter psiParameter) {
        if (psiParameter == null) {
            return null;
        }
        String paramName = psiParameter.getName();
        String desc = JavaDocUtils.getParamsDesc(psiMethod.getDocComment(), paramName);
        FieldDefinition definition = new FieldDefinition();
        definition.setName(paramName);
        definition.setLayer(0);
        definition.setDesc(desc);
        PsiType fieldType = psiParameter.getType();
        ConvertFieldType convertFieldType = BaseTypeConstant.getTypeName(fieldType);
        definition.setType(convertFieldType.getTypeName());
        definition.setVal(convertFieldType.getValue());

        boolean require = MyPsiSupport.getPsiAnnotation(psiParameter, CommonConstant.CONSTRAINTS_NOTNULL) != null
                || MyPsiSupport.getPsiAnnotation(psiParameter, CommonConstant.CONSTRAINTS_NOTBLANK) != null
                || MyPsiSupport.getPsiAnnotation(psiParameter, CommonConstant.CONSTRAINTS_NOTMPTY) != null;

        definition.setRequire(require);
        return definition;
    }
}
