package org.zb.plugin.restdoc.definition;

import org.zb.plugin.restdoc.constant.TransTypeEnum;

import java.util.List;

/**
 * @author :  ZhouBin
 * @date :  2019-10-23
 */

public class MethodDefinition {

    /**
     * 方法名
     */
    private String name;

    /**
     * 方法描述
     */
    private String desc;

    /**
     * 方法标题
     */
    private String title;

    /**
     * 返回类型
     */
    private String rtnType;

    /**
     * 返回默认值
     */
    private Object rtnVal;

    /**
     * 返回类型类型标识
     */
    private TransTypeEnum rtnTypeEnum;

    /**
     * 返回描述
     */
    private String rtnDesc;

    /**
     * 方法注释tag
     */
    private List<MethodTag> tagList;

    /**
     * 参数列表
     */
    private List<Param> paramList;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRtnType() {
        return rtnType;
    }

    public void setRtnType(String rtnType) {
        this.rtnType = rtnType;
    }

    public String getRtnDesc() {
        return rtnDesc;
    }

    public void setRtnDesc(String rtnDesc) {
        this.rtnDesc = rtnDesc;
    }

    public List<MethodTag> getTagList() {
        return tagList;
    }

    public void setTagList(List<MethodTag> tagList) {
        this.tagList = tagList;
    }

    public List<Param> getParamList() {
        return paramList;
    }

    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }

    public static class MethodTag {

        /**
         * tag 类型
         */
        private String tagType;

        /**
         * tag名
         */
        private String tagName;

        /**
         * tag 值
         */
        private String tagVal;

        public MethodTag(String tagType, String tagName, String tagVal) {
            this.tagType = tagType;
            this.tagName = tagName;
            this.tagVal = tagVal;
        }

        public String getTagType() {
            return tagType;
        }

        public void setTagType(String tagType) {
            this.tagType = tagType;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getTagVal() {
            return tagVal;
        }

        public void setTagVal(String tagVal) {
            this.tagVal = tagVal;
        }
    }

    public static class Param {

        /**
         * 参数名
         */
        private String paramName;

        /**
         * 参数类型
         */
        private String paramType;

        /**
         *
         */
        private TransTypeEnum paramTypeEnum;

        /**
         * 注解名
         */
        private List<String> annNameList;


        public Param(String paramName, String paramType, TransTypeEnum paramTypeEnum, List<String> annNameList) {
            this.paramName = paramName;
            this.paramType = paramType;
            this.annNameList = annNameList;
            this.paramTypeEnum= paramTypeEnum;
        }

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        public String getParamType() {
            return paramType;
        }

        public void setParamType(String paramType) {
            this.paramType = paramType;
        }

        public TransTypeEnum getParamTypeEnum() {
            return paramTypeEnum;
        }

        public void setParamTypeEnum(TransTypeEnum paramTypeEnum) {
            this.paramTypeEnum = paramTypeEnum;
        }

        public List<String> getAnnNameList() {
            return annNameList;
        }

        public void setAnnNameList(List<String> annNameList) {
            this.annNameList = annNameList;
        }
    }

    public static MethodTag bulidMethodTag(String tagType, String tagName, String tagVal) {
        return new MethodTag(tagType, tagName, tagVal);
    }

    public static Param buildParam(String paramName, String paramType, ConvertFieldType paramConvert, List<String> annNameList) {
        return new Param(paramName, paramType,paramConvert.getTransType(), annNameList);
    }


    public TransTypeEnum getRtnTypeEnum() {
        return rtnTypeEnum;
    }

    public void setRtnTypeEnum(TransTypeEnum rtnTypeEnum) {
        this.rtnTypeEnum = rtnTypeEnum;
    }

    public Object getRtnVal() {
        return rtnVal;
    }

    public void setRtnVal(Object rtnVal) {
        this.rtnVal = rtnVal;
    }
}
