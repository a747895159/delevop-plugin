package org.zb.plugin.group.extconfig;


import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author : ZhouBin
 */
public class Test001Action extends AnAction {

    public Test001Action() {
        super("测试001", "我是测试001", AllIcons.Actions.Copy);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        BrowserUtil.browse("https://chat.sophia.uk.eu.org/#/");
    }

}
