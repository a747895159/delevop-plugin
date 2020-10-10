package org.zb.plugin.restdoc.constant;


public enum BodyTypeEnum {
    /**
     *
     */
    RequestBody("JSON对象"),

    FormData("表单提交");

    private String desc;

    BodyTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
