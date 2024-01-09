package org.zb.plugin.runtest;

import org.zb.plugin.putil.HttpUtil;

/**
 * @author : ZhouBin
 */
public class AA {
    public static void main(String[] args) {
        String s = HttpUtil.get("https://blog.csdn.net/weixin_46501101/article/details/13228970");
        System.out.println(s);
    }
}
