package org.zb.plugin.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.zb.plugin.html2md.HtmlHandlerUtil;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.io.FileOutputStream;


/**
 * HTML 转换成 markdown
 *
 * @author : ZhouBin
 */
public class Html2MarkdownAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        String inputUrl = Messages.showMultilineInputDialog(project, "请输入要抓取的网站Url", "Information", null, Messages.getQuestionIcon()
                , new InputValidator() {
                    @Override
                    public boolean checkInput(String inputString) {
                        return (inputString != null && inputString.trim().startsWith("http"));
                    }

                    @Override
                    public boolean canClose(String inputString) {
                        return checkInput(inputString);
                    }
                });
        if (StringUtils.isBlank(inputUrl) || !inputUrl.startsWith("http")) {
//            Messages.showErrorDialog(project, "URL信息输入错误!", "Information");
            return;
        }
        String[] split = inputUrl.split("\n");
        int sucNum = 0;
        try {
            for (String url : split) {

                MutablePair<String, String> mutablePair = HtmlHandlerUtil.parseHtml(url);
                String title = mutablePair.getLeft();
                String value = mutablePair.getRight();
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream("D:\\data\\" + title + ".md");
                    IOUtils.write(value, fileOutputStream, "utf-8");
                } finally {
                    IOUtils.closeQuietly(fileOutputStream);
                }
                sucNum++;
            }
        } catch (Exception e1) {
            String message = e1.getMessage();
            message = message == null ? "markdown转换解析失败" : message;
            String errMsg = sucNum == 0 ? message : ("解析成功：" + sucNum + ",剩余中断失败原因(" + message + ")");
            Messages.showErrorDialog(project, errMsg, "Information");
            return;
        }
        ToolUtil.notifyMsg("Convert Success.\nPlease go to the 'D:\\data\\'.", NotificationType.INFORMATION, project);

    }
}
