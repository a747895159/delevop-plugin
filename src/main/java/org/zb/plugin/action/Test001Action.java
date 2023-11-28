package org.zb.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;


/**
 * HTML 转换成 markdown
 *
 * @author : ZhouBin
 */
public class Test001Action extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        String input = Messages.showMultilineInputDialog(project, "测试001内容", "information", null, Messages.getQuestionIcon()
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
        Messages.showInfoMessage(project, input, "information");


    }

}
