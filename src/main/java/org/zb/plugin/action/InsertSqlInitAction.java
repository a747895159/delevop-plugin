package org.zb.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.zb.plugin.panel.InsertSqlInitPanel;

/**
 * Insert sql 初始化
 *
 * @author : ZhouBin
 */
public class InsertSqlInitAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        InsertSqlInitPanel panel = new InsertSqlInitPanel(project);
        panel.show();
    }
}
