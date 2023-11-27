package org.zb.plugin.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zb.plugin.html2md.HtmlHandlerUtil;
import org.zb.plugin.restdoc.utils.ToolUtil;
import org.zb.plugin.util.ProjectUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : ZhouBin
 */
public class H2MPanel extends DialogWrapper {
    private JPanel contentPane;
    private JTextField googleFile;
    private JTextField fileNameInput;
    private JButton googleExeChoose;
    private JButton filePathChoose;
    private JTextArea textArea1;

    private InputValidator myValidator;

    /**
     * 项目对象
     */
    private Project project;


    public H2MPanel(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("Html转换为markdown神器");
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
        String googleExeFile = googleFile.getText().trim();
        if (StringUtils.isEmpty(googleExeFile)) {
            Messages.showWarningDialog("Can't Select google exe file!", "配置错误");
            return;
        }
        String filePath = fileNameInput.getText().trim();
        if (StringUtils.isEmpty(filePath)) {
            Messages.showWarningDialog("Can't Select output filePath!", "配置错误");
            return;
        }
        String inputText = textArea1.getText().trim();
        // 针对Linux系统路径做处理
        googleExeFile = googleExeFile.replace("\\", "/");
        filePath = filePath.replace("\\", "/");
        //设置谷歌驱动
        System.setProperty("webdriver.chrome.driver", googleExeFile);
        AtomicInteger sucNum = new AtomicInteger(0);
        try {
            if (inputText.startsWith("http")) {
                HtmlHandlerUtil.outputFileByUrlList(filePath, inputText, sucNum);
            } else {
                HtmlHandlerUtil.outputFileByTag(filePath, inputText);
            }
        } catch (Exception e1) {
            String message = e1.getMessage();
            message = message == null ? "markdown转换解析失败" : message;
            String errMsg = sucNum.get() == 0 ? message : ("解析成功：" + sucNum + ",剩余中断失败原因(" + message + ")");
            Messages.showErrorDialog(project, errMsg, "Information");
            return;
        } finally {
            System.clearProperty("webdriver.chrome.driver");
        }
        ToolUtil.notifyMsg("Convert Success.Please go to the '" + filePath + "'", NotificationType.INFORMATION, project);

    }

    private void initEvent() {
        //选择路径
        googleExeChoose.addActionListener(e -> {
            //将当前选中的model设置为基础路径
            VirtualFile path = ProjectUtils.getBaseDir(project);
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(), project, path);
            if (virtualFile != null) {
                googleFile.setText(virtualFile.getPath());
            }
        });

        filePathChoose.addActionListener(e -> {
            //将当前选中的model设置为基础路径
            VirtualFile path = ProjectUtils.getBaseDir(project);
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project, path);
            if (virtualFile != null) {
                fileNameInput.setText(virtualFile.getPath());
            }
        });
        this.getOKAction().setEnabled(false);
        this.textArea1.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void textChanged(@NotNull DocumentEvent event) {
                enableOkAction();
            }
        });
        this.filePathChoose.addChangeListener((changeListener) -> {
            enableOkAction();
        });
    }

    private void enableOkAction() {
        String text = this.textArea1.getText().trim();
        String outputFilePath = this.fileNameInput.getText().trim();
        this.getOKAction().setEnabled(false);
        if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(outputFilePath)) {
            this.getOKAction().setEnabled(true);
        }
    }
}
