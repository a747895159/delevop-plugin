package org.zb.plugin.restdoc.utils;

import org.zb.plugin.restdoc.definition.FieldDefinition;

import java.util.List;

/**
 * @author : ZhouBin
 * @date : 2022/6/8
 */
public class ContentShowUtil {


    public static String fieldDefinitionTableBody(List<FieldDefinition> fieldDefinitions, boolean requireFlag) {
        StringBuilder sb = new StringBuilder();
        if (fieldDefinitions == null || fieldDefinitions.isEmpty()) {
            return sb.toString();
        }
        for (FieldDefinition definition : fieldDefinitions) {
            String layerChat = getLayerChat(definition.getLayer());
            sb.append("|").append(layerChat).append(definition.getName());
            sb.append("|").append(definition.getType());
            if (requireFlag) {
                sb.append("|").append(definition.isRequire() ? "是" : "否");
            }
            sb.append("|").append(definition.getDesc().replaceAll("\n", "<br>"));
            sb.append("|\n");
            if (definition.getSubFieldDefinitions() != null && !definition.getSubFieldDefinitions().isEmpty()) {
                sb.append(fieldDefinitionTableBody(definition.getSubFieldDefinitions(), requireFlag));
            }
        }
        return sb.toString();
    }

    public static String getLayerChat(int layer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layer; i++) {
            sb.append("- ");
        }
        return sb.toString();
    }

}
