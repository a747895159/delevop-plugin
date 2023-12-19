package org.zb.plugin.group.extconfig;


import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author : ZhouBin
 */
public class Test003Action extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        BrowserUtil.browse("https://chat.sophia.uk.eu.org/#/");
    }

    public Test003Action() {
        super("测试003", null, AllIcons.Actions.GroupByClass);
    }

}
