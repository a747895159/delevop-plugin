package org.zb.plugin.action;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 *
 * @author :  ZhouBin
 * @date :  2019-11-04
 */
public class PersonalInfoAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        BrowserUtil.browse("https://XXXXXXX");
    }
}
