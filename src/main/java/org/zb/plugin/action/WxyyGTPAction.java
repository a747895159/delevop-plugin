package org.zb.plugin.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author : ZhouBin
 */
public class WxyyGTPAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        //百度 文心一言
        BrowserUtil.browse("https://yiyan.baidu.com/");
    }
}
