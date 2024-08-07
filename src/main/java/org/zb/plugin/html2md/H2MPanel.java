package org.zb.plugin.html2md;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zb.plugin.putil.ProjectUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : ZhouBin
 */
public class H2MPanel extends DialogWrapper {

    private static String defaultFilePath = "D:/data/";

    private static String defaultGoogleExeFile = "D:/develop/Tools/chromedriver.exe";

    private JPanel contentPane;
    private JTextField googleFileInput;
    private JTextField fileNameInput;
    private JButton googleExeChoose;
    private JButton filePathChoose;
    private JTextArea textArea1;
    private JComboBox asyncExecBox;

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
        initPanel();
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
        String googleExeFile = googleFileInput.getText().trim();
        String filePath = fileNameInput.getText().trim();
        if (StringUtils.isEmpty(filePath)) {
            Messages.showWarningDialog("请选择文件目录!", "配置错误");
            return;
        }
        Boolean asyncExecFlag = null;
        if (asyncExecBox.getSelectedItem() != null) {
            asyncExecFlag = ExecType.getAsyncExecFlag((String) asyncExecBox.getSelectedItem());
        }
        String inputText = textArea1.getText().trim();
        // 针对Linux系统路径做处理
        filePath = filePath.replace("\\", "/");

        if (StringUtils.isNotBlank(googleExeFile)) {
            googleExeFile = googleExeFile.replace("\\", "/");
            //设置谷歌驱动
            System.setProperty("webdriver.chrome.driver", googleExeFile);
        }
        //追加文件最后的目录
        filePath = (filePath.endsWith("/") || filePath.endsWith("\\")) ? filePath : (filePath + "/");
        defaultFilePath = filePath;
        defaultGoogleExeFile = googleExeFile;

        AtomicInteger sucNum = new AtomicInteger(0);
        try {
            if (inputText.startsWith("http")) {
                HtmlHandlerUtil.outputFileByUrlList(filePath, inputText, sucNum, asyncExecFlag);
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
            if (StringUtils.isNotBlank(googleExeFile)) {
                System.clearProperty("webdriver.chrome.driver");
            }
        }
        Messages.showInfoMessage(project, "转换成功,请到" + filePath + "目录查看", "success");
//        ToolUtil.notifyMsg("Convert Success.Please go to the '" + filePath + "'", NotificationType.INFORMATION, project);

    }

    private void initEvent() {
        //选择路径
        googleExeChoose.addActionListener(e -> {
            //将当前选中的model设置为基础路径
            VirtualFile path = ProjectUtils.getBaseDir(project);
            VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(), project, path);
            if (virtualFile != null) {
                googleFileInput.setText(virtualFile.getPath());
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
        DocumentAdapter adapter = new DocumentAdapter() {
            @Override
            public void textChanged(@NotNull DocumentEvent event) {
                enableOkAction();
            }
        };
        this.textArea1.getDocument().addDocumentListener(adapter);
        this.fileNameInput.getDocument().addDocumentListener(adapter);
    }

    private void enableOkAction() {
        String text = this.textArea1.getText().trim();
        String inputStr = this.fileNameInput.getText().trim();
        this.getOKAction().setEnabled(false);
        if (StringUtils.isNotBlank(text) && StringUtils.isNotBlank(inputStr)) {
            this.getOKAction().setEnabled(true);
        }
    }

    private void initPanel() {
        if (StringUtils.isNotBlank(defaultGoogleExeFile)) {
            googleFileInput.setText(defaultGoogleExeFile);
        }
        if (StringUtils.isNotBlank(defaultFilePath)) {
            fileNameInput.setText(defaultFilePath);
        }
        asyncExecBox.setModel(new DefaultComboBoxModel<>(Arrays.stream(ExecType.values()).map(ExecType::getDesc).toArray()));
        asyncExecBox.setSelectedIndex(0);
    }

    public enum ExecType {

        E0("默认", null),
        E1("同步", true),
        E2("异步", false);

        /**
         *
         */
        public final String desc;

        public final Boolean execFlag;

        ExecType(String desc, Boolean execFlag) {
            this.desc = desc;
            this.execFlag = execFlag;
        }

        public String getDesc() {
            return desc;
        }

        public Boolean getExecFlag() {
            return execFlag;
        }

        public static Boolean getAsyncExecFlag(String desc) {
            for (ExecType item : ExecType.values()) {
                if (StringUtils.equals(desc, item.getDesc())) {
                    return item.getExecFlag();
                }
            }
            return null;
        }
    }
}
