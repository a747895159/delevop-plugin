package org.zb.plugin.action;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 行转列文本处理
 * @author : ZhouBin
 */
public class TextFormatAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        String input = Messages.showMultilineInputDialog(project, "", "请输入文本串", null, Messages.getQuestionIcon()
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
        try {
            List<String> list = new ArrayList<>();
            String[] split = input.split("\n");
            for (String s : split) {
                if (StringUtils.isNotBlank(s)) {
                    list.add(s.trim());
                }
            }
            String convertStr = JSONObject.toJSONString(list);
//            new MessagePanel(project, convertStr).show();
            ToolUtil.writeClipboard(convertStr,project);
        } catch (Exception exception) {
            Messages.showErrorDialog(project, "处理失败", "Information");
        }
    }
}