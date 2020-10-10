package org.zb.plugin.restdoc.definition;

import java.util.List;

public class FieldDefinition {

    private String name;

    private String desc;

    private String type;

    private boolean require;

    private Integer layer;

    private List<FieldDefinition> subFieldDefinitions;

    private Object val;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public List<FieldDefinition> getSubFieldDefinitions() {
        return subFieldDefinitions;
    }

    public void setSubFieldDefinitions(List<FieldDefinition> subFieldDefinitions) {
        this.subFieldDefinitions = subFieldDefinitions;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }
}
