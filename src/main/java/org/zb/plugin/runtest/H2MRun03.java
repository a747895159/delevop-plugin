package org.zb.plugin.runtest;


import org.zb.plugin.html2md.HtmlHandlerUtil;

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
//        MutablePair<String, String> convert = HtmlHandlerUtil.parseHtml("https://blog.csdn.net/crazymakercircle/article/details/125017316?spm=1001.2014.3001.5502", "");
//        String title = convert.getLeft();
//        String value = convert.getRight();
//        IOUtils.write(value, Files.newOutputStream(Paths.get("D:\\data\\" + title + ".md")), "utf-8");
//        System.out.println(value);

        parseHTML();
    }

    private static void parseHTML(){
        String html ="<blockquote><ol><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的字节跳动商业化一面的原题：进程和线程区别，线程共享内存和进程共享内存的区别</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的小米春招同学 K 一面面试原题：协程和线程和进程的区别</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的字节跳动面经同学 1 Java 后端技术一面面试原题：线程和进程有什么区别？</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的华为 OD 面经同学 1 一面面试原题：对于多线程编程的了解?</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的美团面经同学 2 Java 后端技术一面面试原题：进程和线程的区别？</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的华为面经同学 9 Java 通用软件开发一面面试原题：进程和线程的区别</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的 小公司面经合集好未来测开面经同学 3 测开一面面试原题：进程和线程的区别</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的招商银行面经同学 6 招银网络科技面试原题：进程和线程的区别？</li><li><a href=\"https://javabetter.cn/zhishixingqiu/mianshi.html\" target=\"_blank\" rel=\"noopener noreferrer\">Java 面试指南（付费）<span><svg class=\"external-link-icon\" xmlns=\"http://www.w3.org/2000/svg\" aria-hidden=\"true\" focusable=\"false\" x=\"0px\" y=\"0px\" viewBox=\"0 0 100 100\" width=\"15\" height=\"15\"><path fill=\"currentColor\" d=\"M18.8,85.1h56l0,0c2.2,0,4-1.8,4-4v-32h-8v28h-48v-48h28v-8h-32l0,0c-2.2,0-4,1.8-4,4v56C14.8,83.3,16.6,85.1,18.8,85.1z\"></path><polygon fill=\"currentColor\" points=\"45.7,48.7 51.3,54.3 77.2,28.5 77.2,37.2 85.2,37.2 85.2,14.9 62.8,14.9 62.8,22.9 71.5,22.9\"></polygon></svg><span class=\"external-link-icon-sr-only\">open in new window</span></span></a>收录的用友面试原题：线程和进程的区别</li></ol></blockquote>";

        String s = HtmlHandlerUtil.parseHtmlTag(html);
        System.out.println(s);

    }
}
