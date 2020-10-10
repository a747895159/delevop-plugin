package org.zb.plugin.restdoc.parser;

import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtil;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.action.Bean2JsonAction;
import org.zb.plugin.restdoc.constant.BaseTypeConstant;
import org.zb.plugin.restdoc.constant.BodyTypeEnum;
import org.zb.plugin.restdoc.constant.SpringConstant;
import org.zb.plugin.restdoc.definition.FieldDefinition;
import org.zb.plugin.restdoc.definition.KV;
import org.zb.plugin.restdoc.definition.RestFulDefinition;
import org.zb.plugin.restdoc.parser.translator.TypeTranslator;
import org.zb.plugin.restdoc.utils.JavaDocUtils;
import org.zb.plugin.restdoc.utils.MyPsiSupport;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

public class RestParser extends Parser {

    private PsiMethod psiMethod;

    private RestFulDefinition definition;

    public RestParser(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        this.definition = new RestFulDefinition();
    }

    public RestFulDefinition getDefinition() {
        return definition;
    }

    @Override
    public void parseDefinition() {
        RestFulDefinition definition = new RestFulDefinition();
        definition.setUri(ingestUri());
        definition.setHttpMethod(getHttpMethod());
        String desc = getMethodDesc();
        definition.setDesc(desc);
        definition.setMethodDefinition(MyPsiSupport.getMethodDefinition(this.psiMethod));
        definition.setName(psiMethod.getName());
        definition.setControllerDesc(getControllerDesc());
        BodyTypeEnum bodyTypeEnum = getRequestBodyType();
        definition.setRequestBodyTypeEnum(bodyTypeEnum);
        if (bodyTypeEnum == BodyTypeEnum.RequestBody) {
            definition.setRequest(getRequestBodyDefinitions());
            definition.setRequestJson(getRequestBodyJson());
        } else {
            definition.setRequest(getRequestParamsDefinitions());
        }
        definition.setResponse(getResponseDefinitions());
        definition.setResponseJson(getResponseJson());
        this.definition = definition;

    }


    /**
     * 获得返回参数
     *
     * @return
     */
    private List<FieldDefinition> getResponseDefinitions() {
        ObjectParser objectParser = new ObjectParser(psiMethod.getReturnType(), psiMethod.getProject(), 0);
        objectParser.parseDefinition();
        List<FieldDefinition> responseDefinitions = objectParser.getFieldDefinitions();
        return responseDefinitions;
    }

    private String getResponseJson() {
        PsiType psiType=psiMethod.getReturnType();
        if(psiType==null){
            return null;
        }
        if(BaseTypeConstant.isNormalType(psiType)){
            return null;
        }
        PsiClass psiClass =MyPsiSupport.getPsiClass(psiType);
        String pName= psiClass.getQualifiedName();
        if(TypeTranslator.collectionTypeList.contains(pName)){
            PsiType iterableType = PsiUtil.extractIterableTypeParameter(psiType, false);
            PsiClass iterableClass = MyPsiSupport.getPsiClass(iterableType);
            if (iterableClass != null) {
                List<KV> list = Lists.newArrayList(Bean2JsonAction.getFields(iterableClass));
                return ToolUtil.toPrettyJson(list);
            }
        }
        return Bean2JsonAction.getFields(psiClass).toPrettyJson();
    }

    private String getRequestBodyJson() {
        PsiParameter psiParameter = getRequestBodyParam();
        PsiClass psiClass = MyPsiSupport.getPsiClass(psiParameter.getType());
        if (psiClass != null) {
            return Bean2JsonAction.getFields(psiClass).toPrettyJson();
        }
        return null;
    }



    private List<FieldDefinition> getRequestBodyDefinitions() {
        PsiParameter psiParameter = getRequestBodyParam();
        if (psiParameter != null) {
            ObjectParser objectParser = new ObjectParser(psiParameter.getType(), psiMethod.getProject(), 0);
            objectParser.parseDefinition();
            return objectParser.getFieldDefinitions();
        }
        return new ArrayList<>();
    }

    /**
     * Request Parameter 获得请求参数
     *
     * @return
     */
    private List<FieldDefinition> getRequestParamsDefinitions() {
        PsiParameter[] psiParameter = this.psiMethod.getParameterList().getParameters();
        if (psiParameter != null) {
            ParameterParser parameterParser = new ParameterParser(this.psiMethod);
            parameterParser.parseDefinition();
            List<FieldDefinition> responseDefinitions = parameterParser.getFieldDefinitions();
            return responseDefinitions;
        }
        return new ArrayList<>();
    }


    /**
     * 获得参数请求类型
     *
     * @return
     */
    public BodyTypeEnum getRequestBodyType() {
        PsiParameter[] parameters = this.psiMethod.getParameterList().getParameters();
        if (parameters != null && parameters.length > 0) {
            for (PsiParameter psiParameter : parameters) {
                PsiAnnotation annotation = MyPsiSupport.getPsiAnnotation(psiParameter, SpringConstant.ANNOTATION_REQUESTBODY);
                if (annotation != null) {
                    return BodyTypeEnum.RequestBody;
                }
            }
        }
        return BodyTypeEnum.FormData;
    }


    /**
     * 获得参数请求类型
     *
     * @return
     */
    public PsiParameter getRequestBodyParam() {
        PsiParameter[] parameters = this.psiMethod.getParameterList().getParameters();
        if (parameters != null && parameters.length > 0) {
            for (PsiParameter psiParameter : parameters) {
                PsiAnnotation annotation = MyPsiSupport.getPsiAnnotation(psiParameter, SpringConstant.ANNOTATION_REQUESTBODY);
                if (annotation != null) {
                    return psiParameter;
                }
            }
        }
        return null;
    }


    /**
     * 获得方法描述
     *
     * @return
     */
    private String getMethodDesc() {
        return JavaDocUtils.getText(psiMethod.getDocComment());
    }

    /**
     * 获得Controller 的描述
     *
     * @return
     */
    private String getControllerDesc() {
        return JavaDocUtils.getText(psiMethod.getContainingClass().getDocComment());
    }


    /***
     * 获得Http请求方法
     * @return
     */
    private String getHttpMethod() {
        String httpMethod = "Get";
        PsiAnnotation reqMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringConstant.ANNOTATION_REQUESTMAPPING);
        if (reqMapAn != null) {
            String methodVal = MyPsiSupport.getPsiAnnotationValueByAttr(reqMapAn, "method");
            if (StringUtils.isEmpty(methodVal)) {
                httpMethod = "ALL";
            } else {
                try {
                    httpMethod = methodVal.split("\\.")[1];
                } catch (Exception e) {
                    httpMethod = "undefined";
                }
            }
        }
        PsiAnnotation postMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringConstant.ANNOTATION_POSTMAPPING);
        if (postMapAn != null) {
            httpMethod = "POST";
        }
        return httpMethod;
    }


    /**
     * 获得方法级别的Uri
     *
     * @param annotation
     * @return
     */
    private String getPartOfUri(PsiAnnotation annotation) {
        String path = MyPsiSupport.getPsiAnnotationValueByAttr(annotation, "path");
        String val = MyPsiSupport.getPsiAnnotationValueByAttr(annotation, "value");
        String resultPath = path == null || path.length() <= 0 ? val : path;
        String partOfUri = resultPath != null && resultPath.length() > 0 ? resultPath : "";
        return partOfUri;
    }

    /**
     * 获得Uri
     *
     * @return
     */
    private String ingestUri() {

        Module module = ModuleUtil.findModuleForPsiElement(this.psiMethod);
        String contextPart = "";
        PsiFile[] contextFiles = FilenameIndex.getFilesByName(psiMethod.getProject(), "application.yml", GlobalSearchScope.moduleScope(module));
        if (contextFiles == null || contextFiles.length == 0) {
            contextFiles = FilenameIndex.getFilesByName(psiMethod.getProject(), "application.properties", GlobalSearchScope.moduleScope(module));
        }
        if (contextFiles != null && contextFiles.length > 0) {
            for (PsiFile psiFile : contextFiles) {
                if (psiFile.getName().contains(".yml") || psiFile.getName().contains(".yaml")) {
                    YamlParser yamlParser = new YamlParser(psiFile);
                    contextPart = yamlParser.findProperty("server", "servlet", "context-path");
                    if (StringUtils.isEmpty(contextPart)) {
                        contextPart = yamlParser.findProperty("server", "context-path");
                    }
                    if (!StringUtils.isEmpty(contextPart)) {
                        break;
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(contextPart)) {
            if (contextPart.indexOf("/") != 0) {
                contextPart = "/" + contextPart;
            }
        }
        String methodUriPart = "";
        PsiAnnotation getMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringConstant.ANNOTATION_GETMAPPING);
        if (getMapAn != null) {
            methodUriPart = getPartOfUri(getMapAn);
        }

        PsiAnnotation reqMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringConstant.ANNOTATION_REQUESTMAPPING);
        if (reqMapAn != null) {
            methodUriPart = getPartOfUri(reqMapAn);
        }

        PsiAnnotation postMapAn = MyPsiSupport.getPsiAnnotation(psiMethod, SpringConstant.ANNOTATION_POSTMAPPING);
        if (postMapAn != null) {
            methodUriPart = getPartOfUri(postMapAn);
        }
        String classUriPart = "";
        PsiAnnotation clzReqMapAn = MyPsiSupport.getPsiAnnotation(psiMethod.getContainingClass(), SpringConstant.ANNOTATION_REQUESTMAPPING);
        if (clzReqMapAn != null) {
            classUriPart = getPartOfUri(clzReqMapAn);
        }
        String uri = (contextPart + "/" + classUriPart + "/" + methodUriPart).replaceAll("///", "/").replaceAll("//", "/");
        return uri.lastIndexOf("/") == uri.length() - 1 ? uri.substring(0, uri.length() - 1) : uri;
    }

}
