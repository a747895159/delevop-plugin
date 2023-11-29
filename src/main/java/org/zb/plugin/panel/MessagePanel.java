package org.zb.plugin.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author : ZhouBin
 */
public class MessagePanel extends DialogWrapper {

    private JPanel contentPane;
    private JTextArea textArea1;

    /**
     * 项目对象
     */
    private Project project;


    public MessagePanel(@Nullable Project project, String title, String msg, int width, int height) {
        super(project);
        initPanel(project, title, msg, width, height);
    }

    public MessagePanel(@Nullable Project project, String msg, int width, int height) {
        super(project);
        initPanel(project, "Success", msg, width, height);

    }

    public MessagePanel(@Nullable Project project, String msg) {
        super(project);
        initPanel(project, "Success", msg, 600, 400);

    }

    private void initPanel(Project project, String title, String msg, int width, int height) {
        this.project = project;
        setTitle(title);
        contentPane.setPreferredSize(new Dimension(width, height));
        init();
        textArea1.setEditable(false);
        textArea1.setText(msg);
    }


    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        return this.contentPane;
    }


}
