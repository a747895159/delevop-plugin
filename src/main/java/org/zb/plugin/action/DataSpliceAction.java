package org.zb.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import org.zb.plugin.panel.DataSplicePanel;

/**
 * 数据模版转换处理
 * @author : ZhouBin
 */
public class DataSpliceAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        DataSplicePanel panel = new DataSplicePanel(project);
        panel.show();
    }
}

