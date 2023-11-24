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
import org.zb.plugin.html2md.Constant;
import org.zb.plugin.html2md.HtmlHandlerUtil;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.io.FileOutputStream;
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
                parseUrl(filePath, input, sucNum);
            } else {
                parseHtml(filePath, input);
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

    private void parseUrl(String filePath, String input, AtomicInteger sucNum) throws Exception {
        String[] split = input.split("\n");
        for (String url : split) {
            MutablePair<String, String> mutablePair = HtmlHandlerUtil.parseHtml(url);
            String title = mutablePair.getLeft();
            String value = mutablePair.getRight();
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(filePath + title + ".md");
                IOUtils.write(value, fileOutputStream, "utf-8");
            } finally {
                IOUtils.closeQuietly(fileOutputStream);
            }
            sucNum.addAndGet(1);
        }
    }

    private void parseHtml(String filePath, String inputHtmlTag) throws Exception {
        String value = HtmlHandlerUtil.parseHtmlTag(inputHtmlTag);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath + "Tag标签文档.md");
            IOUtils.write(value, fileOutputStream, "utf-8");
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }
}
