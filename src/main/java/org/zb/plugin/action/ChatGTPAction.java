package org.zb.plugin.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * ChatGTP 链接地址
 *
 * @author :  ZhouBin
 * @date :  2019-11-04
 */
public class ChatGTPAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        //ChatGPT
        BrowserUtil.browse("https://chat.sophia.uk.eu.org/#/");
    }
}
