package org.zb.plugin.html2md;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求url页面处理工具类
 *
 * @author : ZhouBin
 */
public class HtmlHandlerUtil {

    public static final String CATALOG = "## 文章目录";

    /**
     * 根据URL规则进行解析
     */
    public static MutablePair<String, String> parseHtml(String url) {
        HostRuleEnum ruleEnum = HostRuleEnum.findHost(url);
        return parseRemoteUrlHtml(url, ruleEnum, null);

    }

    /**
     * 根据元素Id进行解析，若为空时，按照url方式解析
     */
    public static MutablePair<String, String> parseHtml(String url, String eleId) {
        HostRuleEnum ruleEnum = HostRuleEnum.findHost(url);
        return parseRemoteUrlHtml(url, ruleEnum, eleId);
    }

    private static MutablePair<String, String> parseRemoteUrlHtml(String url, HostRuleEnum ruleEnum, String eleId) {
        try {
            MutablePair<String, String> pair = new MutablePair<>();
            // 获取正文内容，
            Document doc;
            if (BooleanUtils.isTrue(ruleEnum.getSyncFlag())) {
                // 获取正文内容，
                doc = Jsoup.parse(new URL(url), 5000);
            } else {
                //异步方式获取内容
                doc = asyncHtml(url);
            }

            String title;
            Element ele = null;
            if (StringUtils.isNotBlank(eleId)) {
                title = H2MConvertUtil.fetchTitle(doc);
                ele = doc.getElementById(eleId);
            } else {
                title = getTitle(doc, ruleEnum.getTitleSplit());
                if (Objects.equals(EleTagEnum.ID, ruleEnum.getEleTag())) {
                    String[] str1 = ruleEnum.getEleTagVal().split("\\|");
                    if (str1.length > 1) {
                        for (String s : str1) {
                            ele = doc.getElementById(s);
                            if (ele != null) {
                                break;
                            }
                        }
                    } else {
                        ele = doc.getElementById(ruleEnum.getEleTagVal());
                    }
                } else if (Objects.equals(EleTagEnum.CSS, ruleEnum.getEleTag())) {
                    ele = doc.select("." + ruleEnum.getEleTagVal()).get(0);
                } else if (Objects.equals(EleTagEnum.TAG, ruleEnum.getEleTag())) {
                    ele = doc.getElementsByTag(ruleEnum.getEleTagVal()).get(0);
                } else {
                    ele = doc.getElementsByTag(ruleEnum.getEleTagVal()).get(0);
                }
                handlerWebSite(ruleEnum, ele);
            }
            //获取正文内容元素
            String content = H2MConvertUtil.getTextContent(ele);
            if (content != null && content.contains(CATALOG)) {
                String[] split = content.split(CATALOG);
                content = CATALOG + "\n[TOC]\n" + split[1];
            }
            content += "\n \n \n [原文地址](" + url + ") ";

            pair.setRight(content);
            pair.setLeft(title.replaceAll("\\\\", "").replaceAll("/", ""));
            return pair;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseHtmlTag(String html) {
        Document ele = Jsoup.parseBodyFragment(html);
        //获取正文内容元素
        String content = H2MConvertUtil.getTextContent(ele);
        if (content != null && content.contains(CATALOG)) {
            String[] split = content.split(CATALOG);
            content = CATALOG + "\n[TOC]\n" + split[1];
        }
        return content;
    }


    public static String getTitle(Document doc, String titleSplit) {
        String title = H2MConvertUtil.fetchTitle(doc);
        if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(titleSplit)) {
            return title.split(titleSplit)[0];
        }
        return title;
    }

    private static void handlerWebSite(HostRuleEnum ruleEnum, Element ele) {
        if (ruleEnum == null) {
            return;
        }
        switch (ruleEnum) {
            case CSDN:
                ele.getElementsByTag("script").remove();
                ele.select(".dp-highlighter").remove();
                break;
            case CNBLOG:
                ele.getElementsByTag("script").remove();
                break;
            case WECHAT:
                ele.getElementsByTag("script").remove();
                // 图片标签显示
                ele.getElementsByTag("img").forEach(e -> e.attr("src", e.attr("data-src")));
                // 正文内容展示
                Element jsContent = ele.getElementById("js_content");
                if (jsContent != null) {
                    jsContent.attr("style", "visibility");
                }
                break;
            case BILIBILI:
                ele.getElementsByTag("img").forEach(e -> e.attr("src", e.attr("data-src")));
                break;
            case JS:
                ele.getElementsByTag("img").forEach(e -> e.attr("src", e.attr("data-original-src")));
                break;
            case ZH:
                ele.getElementsByTag("img").forEach(e -> {
                    String attr = e.attr("data-original");
                    if (StringUtils.isNotBlank(attr)) {
                        e.removeAttr("src");
                        e.attr("src", attr);
                    }
                });
                break;
            default:

        }

    }

    /**
     * 异步加载浏览器获取文本内容
     */
    private static Document asyncHtml(String url) throws Exception {
        //System.setProperty("webdriver.chrome.driver", "D:\\develop\\Tools\\chromedriver.exe");
        String chromeDriver = System.getProperty("webdriver.chrome.driver");
        if (StringUtils.isBlank(chromeDriver)) {
            throw new RuntimeException("请配置谷歌浏览器驱动程序!");
        }
        //引入谷歌驱动
        ChromeOptions options = new ChromeOptions();
        //允许所有请求
        options.addArguments("--remote-allow-origins=*");
        WebDriver webDriver = new ChromeDriver(options);
        //启动需要打开的网页
        webDriver.get(url);
        //程序暂停5秒
        Thread.sleep(5000);
        String pageSource = webDriver.getPageSource();
        Document parse = Jsoup.parse(pageSource);
        System.clearProperty("webdriver.chrome.driver");
        webDriver.close();
        return parse;
    }

    public static void outputFileByUrlList(String filePath, String input, AtomicInteger sucNum) throws Exception {
        String[] split = input.split("\n");
        for (String url : split) {
            MutablePair<String, String> mutablePair = parseHtml(url);
            String title = mutablePair.getLeft();
            String value = mutablePair.getRight();
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(filePath + title + ".md");
                IOUtils.write(value, fileOutputStream, "utf-8");
            } finally {
                IOUtils.closeQuietly(fileOutputStream);
            }
            sucNum.addAndGet(1);
        }
    }

    public static void outputFileByTag(String filePath, String inputHtmlTag) throws Exception {
        String value = HtmlHandlerUtil.parseHtmlTag(inputHtmlTag);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath + "Tag标签文档.md");
            IOUtils.write(value, fileOutputStream, "utf-8");
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

}
