package org.zb.plugin.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DocumentAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Arrays;

/**
 * @author : ZhouBin
 */
public class DataSplicePanel extends DialogWrapper {

    public static final String TITLE = "数据模板拼接";

    private JPanel contentPane;
    private JTextField textInput1;
    private JTextArea textArea1;

    /**
     * 项目对象
     */
    private Project project;


    public DataSplicePanel(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle(TITLE);
        contentPane.setPreferredSize(new Dimension(600, 400));
        init();
        initEvent();

    }


    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        return this.contentPane;
    }

    @Override
    protected void doOKAction() {
        onOK();
        super.doOKAction();
    }

    /**
     * 确认按钮回调事件
     */
    private void onOK() {
        String inputStr = textInput1.getText().trim();
        String textStr = textArea1.getText().trim();
        if (StringUtils.isBlank(inputStr) || StringUtils.isBlank(textStr)) {
            Messages.showWarningDialog("请输入文本内容!", "参数错误");
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            String[] split = textStr.split("\n");
            for (String line : split) {
                String format = String.format(inputStr, Arrays.stream(line.split(",")).map(String::trim).toArray());
                sb.append(format).append("\n");
            }
            new MessagePanel(project, sb.toString()).show();
        } catch (Exception e1) {
            Messages.showErrorDialog(project, TITLE + "失败", "Information");
        }
    }

    private void initEvent() {
        this.getOKAction().setEnabled(false);
        this.textArea1.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void textChanged(@NotNull DocumentEvent event) {
                enableOkAction();
            }
        });
        this.textInput1.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                enableOkAction();
            }
        });
        textInput1.requestFocus();
    }

    private void enableOkAction() {
        String text = this.textArea1.getText().trim();
        String inputStr = this.textInput1.getText().trim();
        this.getOKAction().setEnabled(false);
        if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(inputStr)) {
            this.getOKAction().setEnabled(true);
        }
    }

}
