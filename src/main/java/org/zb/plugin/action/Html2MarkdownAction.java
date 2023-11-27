package org.zb.plugin.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.html2md.Constant;
import org.zb.plugin.html2md.HtmlHandlerUtil;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * HTML 转换成 markdown
 *
 * @author : ZhouBin
 */
public class Html2MarkdownAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        String input = Messages.showMultilineInputDialog(project, "请输入要抓取的网站Url或者网页Tag内容", "Information", null, Messages.getQuestionIcon()
                , new InputValidator() {
                    @Override
                    public boolean checkInput(String inputString) {
                        return StringUtils.isNotBlank(inputString);
                    }

                    @Override
                    public boolean canClose(String inputString) {
                        return checkInput(inputString);
                    }
                });
        if (input == null || StringUtils.isBlank(input.trim())) {
            return;
        }
        String filePath = Constant.getPath();
        AtomicInteger sucNum = new AtomicInteger(0);
        try {
            if (input.startsWith("http")) {
                HtmlHandlerUtil.outputFileByUrlList(filePath, input, sucNum);
            } else {
                HtmlHandlerUtil.outputFileByTag(filePath, input);
            }
        } catch (Exception e1) {
            String message = e1.getMessage();
            message = message == null ? "markdown转换解析失败" : message;
            String errMsg = sucNum.get() == 0 ? message : ("解析成功：" + sucNum + ",剩余中断失败原因(" + message + ")");
            Messages.showErrorDialog(project, errMsg, "Information");
            return;
        }
        ToolUtil.notifyMsg("Convert Success.Please go to the '" + filePath + "'", NotificationType.INFORMATION, project);


    }

}
