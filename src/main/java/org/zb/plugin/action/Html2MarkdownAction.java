package org.zb.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.zb.plugin.html2md.H2MPanel;

/**
 * @author : ZhouBin
 */
public class Html2MarkdownAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        H2MPanel panel = new H2MPanel(project);
        panel.show();
    }
}
