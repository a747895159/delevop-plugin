package org.zb.plugin.runtest;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.zb.plugin.html2md.HtmlHandlerUtil;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 异步-selenium方式获取页面内容
 *
 * @author : ZhouBin
 */
public class H2MRun03 {

    public static void main(String[] args) throws Exception {
        /*
         * https://blog.csdn.net/crazymakercircle/article/details/125017316?spm=1001.2014.3001.5502
         * https://thinkwon.blog.csdn.net/article/details/104397367
         * 异步文章
         * https://segmentfault.com/a/1190000018835760
         * https://www.mdnice.com/writing/2a53472ff62e47e78f4d8dc005e91be1
         * https://www.toutiao.com/article/6730619165612179979
         */

        System.setProperty("webdriver.chrome.driver", "D:\\develop\\Tools\\chromedriver.exe");
        MutablePair<String, String> convert = HtmlHandlerUtil.parseHtml("https://blog.csdn.net/crazymakercircle/article/details/125017316?spm=1001.2014.3001.5502", "");
        String title = convert.getLeft();
        String value = convert.getRight();
        IOUtils.write(value, Files.newOutputStream(Paths.get("D:\\data\\" + title + ".md")), "utf-8");
        System.out.println(value);

//        parseHTML();
    }

    private static void parseHTML(){
        String html ="<p style=\"margin-bottom: 0px;padding-top: 8px;padding-bottom: 8px;letter-spacing: normal;text-align: left;caret-color: rgb(51, 51, 51);outline: 0px;background-color: rgb(255, 255, 255);font-size: 15px;color: rgb(74, 74, 74);font-family: system-ui, -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, &quot;PingFang SC&quot;, &quot;Hiragino Sans GB&quot;, &quot;Microsoft YaHei UI&quot;, &quot;Microsoft YaHei&quot;, Arial, sans-serif;line-height: 1.75em;\"><img class=\"rich_pages wxw-img\" data-ratio=\"0.42685185185185187\" data-src=\"https://mmbiz.qpic.cn/sz_mmbiz_jpg/kNkb0KtfticsJOw5YKtttF0CeibuIpzQLjpZQ8yCwHZma5yMnol885WKnnvdy1kDMr01SUicdp6ibKrls8t3gd0pjQ/640?wx_fmt=jpeg&amp;random=0.27984345744077976\" data-type=\"other\" data-w=\"1080\" style=\"vertical-align: inherit; width: 578px !important; height: auto !important; visibility: visible !important;\" data-original-style=\"vertical-align: inherit;width: 578px;height: auto !important;\" data-index=\"7\" src=\"https://mmbiz.qpic.cn/sz_mmbiz_jpg/kNkb0KtfticsJOw5YKtttF0CeibuIpzQLjpZQ8yCwHZma5yMnol885WKnnvdy1kDMr01SUicdp6ibKrls8t3gd0pjQ/640?wx_fmt=jpeg&amp;random=0.27984345744077976&amp;tp=webp&amp;wxfrom=5&amp;wx_lazy=1&amp;wx_co=1\" _width=\"578px\" crossorigin=\"anonymous\" alt=\"图片\" data-fail=\"0\"><span style=\"font-family: &quot;Helvetica Neue&quot;, Helvetica, &quot;Hiragino Sans GB&quot;, &quot;Apple Color Emoji&quot;, &quot;Emoji Symbols Font&quot;, &quot;Segoe UI Symbol&quot;, Arial, sans-serif;font-size: 14px;\">以上流程基本遵循 发现问题 --&gt; 原理分析 --&gt; 解决问题 --&gt; 升华(编程规范)。</span></p>";


        String s = HtmlHandlerUtil.parseHtmlTag(html);
        System.out.println(s);

    }
}
