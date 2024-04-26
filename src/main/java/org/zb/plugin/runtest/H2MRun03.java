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
        MutablePair<String, String> convert = HtmlHandlerUtil.parseHtml("https://www.jianshu.com/p/0ff1b6a3da36", "");
        String title = convert.getLeft();
        String value = convert.getRight();
        IOUtils.write(value, new FileOutputStream("D:\\data\\" + title + ".md"), "utf-8");
        System.out.println(value);

//        parseHTML();
    }

    private static void parseHTML(){
        String html ="<html><body><blockquote>\n" +
                "<ol>\n" +
                "<li><p>producer 向<code>topic</code>发送消息，同一个<code>topic</code>下存在多个队列，<code>RocketMQ</code> 的生产者在默认发送消息时轮询选取其中一个队列进行发送，会导致消息分散到两个队列上</p></li>\n" +
                "<li><p>broker 上的消息只有在同一个队列中消息才是顺序读取的</p></li>\n" +
                "<li><p>消费组消费消息时，每一个consumer 单独消费broker 上的一个队列，一般情况下一个consumer 一个进程，不同进程消费不能保证消费的顺序性</p></li>\n" +
                "<li><p>同一个进程下，通常会配置多个线程消费，多个线程消费的情况下没办法保证消费顺序</p></li>\n" +
                "</ol>\n" +
                "</blockquote></body></html>";

        String s = HtmlHandlerUtil.parseHtmlTag(html);
        System.out.println(s);

    }
}
