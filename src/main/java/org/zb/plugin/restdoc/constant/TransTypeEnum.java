package org.zb.plugin.restdoc.constant;

/**
 * @author :  ZhouBin
 * @date :  2019-11-01
 */
public enum TransTypeEnum {
    /**
     *
     */
    PRIMITIVE_TYPE(0,"基本类型"),

    ARRAY(1,"数组"),

    LIST(2,"集合列表"),

    ENUM(3,"枚举"),

    OTHER_OBJECT(4,"其他对象"),

    VOID(5,"无返回值");

    private int code;

    private String desc;

    TransTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
