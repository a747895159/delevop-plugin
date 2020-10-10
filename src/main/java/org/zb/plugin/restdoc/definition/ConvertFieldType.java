package org.zb.plugin.restdoc.definition;

import org.zb.plugin.restdoc.constant.TransTypeEnum;

/**
 * @author :  ZhouBin
 * @date :  2019-10-31
 */
public class ConvertFieldType {

    private String typeName;

    private Object value;

    private TransTypeEnum transType;


    public ConvertFieldType(String typeName, Object value, TransTypeEnum transType) {
        this.typeName = typeName;
        this.value = value;
        this.transType = transType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public TransTypeEnum getTransType() {
        return transType;
    }

    public void setTransType(TransTypeEnum transType) {
        this.transType = transType;
    }
}
