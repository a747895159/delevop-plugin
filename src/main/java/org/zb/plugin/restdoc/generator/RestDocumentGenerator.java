package org.zb.plugin.restdoc.generator;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.restdoc.constant.BaseTypeConstant;
import org.zb.plugin.restdoc.constant.BodyTypeEnum;
import org.zb.plugin.restdoc.constant.SpringConstant;
import org.zb.plugin.restdoc.constant.TransTypeEnum;
import org.zb.plugin.restdoc.definition.FieldDefinition;
import org.zb.plugin.restdoc.definition.RestFulDefinition;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestDocumentGenerator {

    private RestFulDefinition definition;

    public RestDocumentGenerator(RestFulDefinition definition) {
        this.definition = definition;
    }

    public String generate() {
        ToolUtil.logInfo("definition\n", new Gson().toJson(definition));

        StringBuilder docContent = new StringBuilder();
        docContent.append(interfaceNamePart());
        docContent.append("\n");
        docContent.append(urlPart());
        docContent.append("\n");
        docContent.append(methodPart());
        docContent.append("\n");
        docContent.append("\n");

        docContent.append(requestPart());
        docContent.append("\n");
        docContent.append("\n");
        docContent.append(requestJsonExample());
        docContent.append(responsePart());
        docContent.append("\n");
        docContent.append("\n");
        docContent.append(responseJsonExample());
        return docContent.toString();
    }

    public String interfaceNamePart() {
        String title = definition.getMethodDefinition().getTitle();
        if (StringUtils.isBlank(title)) {
            title = "请输入接口文档标题";
        }
        return "# " + title + "\n";
    }

    public String urlPart() {
        StringBuilder sb = new StringBuilder("**请求URL：** \n");
        sb.append("- `" + this.definition.getUri() + " `\n");
        return sb.toString();
    }

    public String methodPart() {
        StringBuilder sb = new StringBuilder("**请求方式：**\n");
        sb.append("- " + this.definition.getHttpMethod() + "   " + this.definition.getRequestBodyTypeEnum().getDesc() + "\n");
        return sb.toString();
    }

    public String requestPart() {
        StringBuilder sb = new StringBuilder("###请求参数\n \n");
        List<FieldDefinition> fieldDefinitions = this.definition.getRequest();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            sb.append("- 无参数\n");
            return sb.toString();
        }
        sb.append("|参数名|类型|必选|说明|\n");
        sb.append("|:----    |:---|:----- |-----   |\n");

        sb.append(this.fieldDefinitionTableBody(fieldDefinitions, true));
        return sb.toString();
    }

    public String responsePart() {
        StringBuilder sb = new StringBuilder("###响应参数\n \n");
        List<FieldDefinition> fieldDefinitions = this.definition.getResponse();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            if (TransTypeEnum.VOID == definition.getMethodDefinition().getRtnTypeEnum()) {
                sb.append("- 无返回值\n");
            } else {
                String desc = definition.getMethodDefinition().getRtnDesc();
                String val = ToolUtil.toJson(definition.getMethodDefinition().getRtnVal());
                sb.append("|类型|说明|响应示例|\n");
                sb.append("|:----|:----|-----|\n");
                sb.append("|").append(definition.getMethodDefinition().getRtnType()).append("|");
                sb.append(desc == null ? "" : desc).append("|");
                sb.append(val == null ? "" : val).append("|");
            }
            return sb.toString();
        } else {
            sb.append("|参数名|类型|说明|\n");
            sb.append("|:----|:----|-----|\n");
            sb.append(this.fieldDefinitionTableBody(fieldDefinitions, false));
        }
        return sb.toString();
    }

    public String fieldDefinitionTableBody(List<FieldDefinition> fieldDefinitions, boolean requireFlag) {
        StringBuilder sb = new StringBuilder();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            return sb.toString();
        }
        for (FieldDefinition definition : fieldDefinitions) {
            String layerChat = this.getLayerChat(definition.getLayer());
            sb.append("|" + layerChat + definition.getName());
            sb.append("|" + definition.getType());
            if (requireFlag) {
                sb.append("|" + (definition.isRequire() ? "是" : "否"));
            }
            sb.append("|" + definition.getDesc().replaceAll("\n", "<br>"));
            sb.append("|\n");
            if (definition.getSubFieldDefinitions() != null && !definition.getSubFieldDefinitions().isEmpty()) {
                sb.append(this.fieldDefinitionTableBody(definition.getSubFieldDefinitions(), requireFlag));
            }
        }
        return sb.toString();
    }

    public String getLayerChat(int layer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layer; i++) {
            sb.append("- ");
        }
        return sb.toString();
    }

    public String requestJsonExample() {
        if (definition.getRequest() != null && definition.getRequest().size() > 0 && definition.getRequestBodyTypeEnum() == BodyTypeEnum.RequestBody) {
            Map<String, Object> map = parseVal(definition.getRequest());
            /**
             * @RequestBody 目前只支持单对象注解 ,列表中必有值
             */
            TransTypeEnum transTypeEnum=definition.getMethodDefinition().getParamList().stream().
                    filter(o->o.getAnnNameList().contains(SpringConstant.ANNOTATION_REQUESTBODY)).findFirst().get().getParamTypeEnum();
            Object o = isArray(transTypeEnum) ? Lists.newArrayList(map) : map;
            return toJson("请求示例",o);
        }
        return "";
    }


    public String responseJsonExample() {
        if (definition.getResponse() != null && definition.getResponse().size() > 0) {
            Map<String, Object> map = parseVal(definition.getResponse());
            Object o = isArray(definition.getMethodDefinition().getRtnTypeEnum()) ? Lists.newArrayList(map) : map;
            return toJson("响应示例",o);
        }
        return "";
    }
    private String toJson(String title,Object o){
        StringBuilder sb = new StringBuilder("###").append(title).append("\n \n");
        sb.append("```\n");
        sb.append(ToolUtil.toPrettyJson(o)).append("\n");
        sb.append("\n");
        sb.append("```\n");
        sb.append("\n");
        sb.append("\n");
        return sb.toString();
    }

    private boolean isArray(TransTypeEnum transTypeEnum) {
        return transTypeEnum == TransTypeEnum.LIST || transTypeEnum == TransTypeEnum.ARRAY;
    }

    private static Map<String, Object> parseVal(List<FieldDefinition> list) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (FieldDefinition fd : list) {
            if (fd.getSubFieldDefinitions() != null && fd.getSubFieldDefinitions().size() > 0) {
                if (fd.getType().contains(BaseTypeConstant.ARRAY_SUFFIX)) {
                    map.put(fd.getName(), Lists.newArrayList(parseVal(fd.getSubFieldDefinitions())));
                } else {
                    map.put(fd.getName(), parseVal(fd.getSubFieldDefinitions()));
                }

            } else {
                map.put(fd.getName(), fd.getVal());
            }
        }
        return map;
    }


}
