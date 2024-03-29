package org.zb.plugin.runtest;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.zb.plugin.html2md.HtmlHandlerUtil;

import java.io.FileOutputStream;

/**
 * 异步-selenium方式获取页面内容
 *
 * @author : ZhouBin
 */
public class H2MRun03 {

    public static void main(String[] args) throws Exception {
        /*
         * https://thinkwon.blog.csdn.net/article/details/104397367
         * 异步文章
         * https://segmentfault.com/a/1190000018835760
         * https://www.mdnice.com/writing/2a53472ff62e47e78f4d8dc005e91be1
         * https://www.toutiao.com/article/6730619165612179979
         */
        System.setProperty("webdriver.chrome.driver", "D:\\develop\\Tools\\chromedriver.exe");
        MutablePair<String, String> convert = HtmlHandlerUtil.parseHtml("https://blog.csdn.net/crazymakercircle/article/details/129098352?spm=1001.2014.3001.5502", "");
        String title = convert.getLeft();
        String value = convert.getRight();
        IOUtils.write(value, new FileOutputStream("D:\\data\\" + title + ".md"), "utf-8");
    }
}
