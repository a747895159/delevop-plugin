package org.zb.plugin.restdoc.definition;

import org.zb.plugin.restdoc.constant.BodyTypeEnum;

import java.util.List;

public class RestFulDefinition {

    private String controllerDesc;

    private String name;

    private String desc;

    private String httpMethod;

    private String uri;

    private BodyTypeEnum requestBodyTypeEnum;

    private MethodDefinition methodDefinition;

    private List<FieldDefinition> request;

    private String requestJson;

    private List<FieldDefinition> response;

    private String responseJson;

    public String getControllerDesc() {
        return controllerDesc;
    }

    public void setControllerDesc(String controllerDesc) {
        this.controllerDesc = controllerDesc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public BodyTypeEnum getRequestBodyTypeEnum() {
        return requestBodyTypeEnum;
    }

    public void setRequestBodyTypeEnum(BodyTypeEnum requestBodyTypeEnum) {
        this.requestBodyTypeEnum = requestBodyTypeEnum;
    }

    public List<FieldDefinition> getRequest() {
        return request;
    }

    public void setRequest(List<FieldDefinition> request) {
        this.request = request;
    }

    public List<FieldDefinition> getResponse() {
        return response;
    }

    public void setResponse(List<FieldDefinition> response) {
        this.response = response;
    }

    public MethodDefinition getMethodDefinition() {
        return methodDefinition;
    }

    public void setMethodDefinition(MethodDefinition methodDefinition) {
        this.methodDefinition = methodDefinition;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }
}
