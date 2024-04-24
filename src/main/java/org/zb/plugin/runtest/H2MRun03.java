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
        MutablePair<String, String> convert = HtmlHandlerUtil.parseHtml("https://blog.51cto.com/u_16213590/8531092", "");
        String title = convert.getLeft();
        String value = convert.getRight();
        IOUtils.write(value, new FileOutputStream("D:\\data\\" + title + ".md"), "utf-8");

//        parseHTML();
    }

    private static void parseHTML(){
        String html ="<pre class=\"language-plain prettyprint\" tabindex=\"0\"><code class=\"language-plain has-numbering\" id=\"code_id_0\">long diff = maxOffsetPy - maxPhyOffsetPulling;  // @1\n" +
                "long memory = (long) (StoreUtil.TOTAL_PHYSICAL_MEMORY_SIZE\n" +
                "                            * (this.messageStoreConfig.getAccessMessageInMemoryMaxRatio() / 100.0));  // @2\n" +
                "getResult.setSuggestPullingFromSlave(diff &gt; memory);   // @3</code><ul class=\"pre-numbering\" style=\"\"><li>1.</li><li>2.</li><li>3.</li><li>4.</li></ul></pre>";

        String s = HtmlHandlerUtil.parseHtmlTag(html);
        System.out.println(s);

    }
}
