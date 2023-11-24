package org.zb.plugin.html2md;

import java.util.Arrays;

/**
 * 请求URL解析规则配置类
 * @author : ZhouBin
 */
public enum HostRuleEnum {

    /**
     *
     */
    CSDN("blog.csdn.net", EleTagEnum.ID, "article_content", true, "-CSDN博客"),

    CNBLOG("cnblogs.com", EleTagEnum.ID, "post_detail", true, " - 博客园"),

    //微信  元素有待优化  js_content img-content
    WECHAT("mp.weixin.qq.com", EleTagEnum.ID, "js_content", true, null),

    //ID方式支持多个元素,以|分割
    CTO_51("blog.51cto.com", EleTagEnum.ID, "markdownContent|container", true, "_51CTO博客"),

    BILIBILI("bilibili.com", EleTagEnum.CSS, "article-content", true, " - 哔哩哔哩"),

    JS("jianshu.com", EleTagEnum.TAG, "article", true, " - 简书"),

    ZH("zhihu.com", EleTagEnum.TAG, "article", true, " - 知乎"),


    UNKNOWN("UNKNOWN", null, "body", true, null),


    /*********************************以下网址都是异步浏览器加载内容*************************************************/

    JRTT("toutiao.com", EleTagEnum.TAG, "article", false, "-今日头条"),

    MDNICE("mdnice.com", EleTagEnum.ID, "writing-content", false, " - mdnice 墨滴"),

    SF("segmentfault.com", EleTagEnum.TAG, "article", false, " - SegmentFault 思否"),

    ;

    /**
     * host域名
     */
    private final String host;

    /**
     * 元素类型
     */
    private final EleTagEnum eleTag;

    /**
     * 元素值
     */
    private final String eleTagVal;

    /**
     * 页面内容 同步/异步标识
     */
    private final Boolean syncFlag;

    /**
     * 标题名分割串
     */
    private final String titleSplit;


    HostRuleEnum(String host, EleTagEnum eleTag, String eleTagVal, Boolean syncFlag, String titleSplit) {
        this.host = host;
        this.eleTag = eleTag;
        this.eleTagVal = eleTagVal;
        this.syncFlag = syncFlag;
        this.titleSplit = titleSplit;
    }

    public String getHost() {
        return host;
    }

    public EleTagEnum getEleTag() {
        return eleTag;
    }

    public String getEleTagVal() {
        return eleTagVal;
    }

    public Boolean getSyncFlag() {
        return syncFlag;
    }

    public String getTitleSplit() {
        return titleSplit;
    }

    public static HostRuleEnum findHost(String url) {
        return Arrays.stream(HostRuleEnum.values()).filter(t -> url.contains(t.getHost())).findFirst().orElse(UNKNOWN);
    }


}
