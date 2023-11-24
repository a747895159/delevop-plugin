package org.zb.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * HTML 转换成 markdown
 *
 * @author : ZhouBin
 */
public class Html2MarkdownAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Bean2JsonAction.isShowComment = !Bean2JsonAction.isShowComment;
        e.getPresentation().setIcon(Bean2JsonAction.isShowComment ? AllIcons.Actions.Checked : null);
    }
}
