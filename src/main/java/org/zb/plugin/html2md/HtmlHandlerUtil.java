package org.zb.plugin.html2md;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
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
    public static MutablePair<String, String> parseHtml(String url, Boolean asyncExecFlag) {
        HostRuleEnum ruleEnum = HostRuleEnum.findHost(url);
        return parseRemoteUrlHtml(url, ruleEnum, null, asyncExecFlag);

    }

    /**
     * 根据元素Id进行解析，若为空时，按照url方式解析
     */
    public static MutablePair<String, String> parseHtml(String url, String eleId) {
        HostRuleEnum ruleEnum = HostRuleEnum.findHost(url);
        return parseRemoteUrlHtml(url, ruleEnum, eleId, ruleEnum.getSyncFlag());
    }

    private static MutablePair<String, String> parseRemoteUrlHtml(String url, HostRuleEnum ruleEnum, String eleId, Boolean asyncExecFlag) {
        try {
            MutablePair<String, String> pair = new MutablePair<>();
            // 获取正文内容，
            Document doc;
            boolean asyncFlag = asyncExecFlag == null ? ruleEnum.getSyncFlag() : asyncExecFlag;
            if (BooleanUtils.isTrue(asyncFlag)) {
                // 获取正文内容，
//                doc = Jsoup.parse(new URL(url), 5000);
                Connection connect = Jsoup.connect(url);
                Map<String, String> headerMap = new HashMap<>(8);
                headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
                connect.headers(headerMap);
                connect.referrer("https://www.baidu.com");
                connect.timeout(5000);
                doc = connect.get();

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
                handlerWebSite(ruleEnum, ele, url);
            }
            //获取正文内容元素
            String content = H2MConvertUtil.getConvertContent(ele);
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
        String content = H2MConvertUtil.getConvertContent(ele);
        if (content != null && content.contains(CATALOG)) {
            String[] split = content.split(CATALOG);
            content = CATALOG + "\n[TOC]\n" + split[1];
        }
        return content;
    }


    public static String getTitle(Document doc, String titleSplit) {
        try {
            String title = H2MConvertUtil.fetchTitle(doc);
            if (StringUtils.isNotBlank(title) && StringUtils.isNotBlank(titleSplit)) {
                title = title.split(titleSplit)[0];
            }
            return title.replaceAll("[\\\\/|:*?\"]", "").split("？")[0];
        } catch (Exception e) {
            return "exception-default";
        }
    }

    private static void handlerWebSite(HostRuleEnum ruleEnum, Element ele, String url) {
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
            case SF:
                ele.getElementsByTag("img").forEach(e -> {
                    String imgSrc = e.attr("src");
                    if (StringUtils.isNotBlank(imgSrc) && !imgSrc.contains(ruleEnum.getHost())) {
                        String[] split = url.split(ruleEnum.getHost());
                        imgSrc = split[0] + ruleEnum.getHost() + imgSrc;
                        e.attr("src", imgSrc);
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
        webDriver.close();
        return parse;
    }

    public static void outputFileByUrlList(String filePath, String input, AtomicInteger sucNum, Boolean asyncExecFlag) throws Exception {
        String[] split = input.split("\n");
        for (String url : split) {
            MutablePair<String, String> mutablePair = parseHtml(url, asyncExecFlag);
            String title = mutablePair.getLeft();
            String value = mutablePair.getRight();

            try {
                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath + title + ".md")) {
                    IOUtils.write(value, fileOutputStream, "utf-8");
                }
            } catch (Exception e) {
                title = "标签文档_" + sucNum.get();
                try (FileOutputStream fileOutputStream = new FileOutputStream(filePath + title + ".md")) {
                    IOUtils.write(value, fileOutputStream, "utf-8");
                }
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
