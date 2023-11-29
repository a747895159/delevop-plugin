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


    public MessagePanel(@Nullable Project project, String title, String msg) {
        super(project);
        this.project = project;
        setTitle(title);
        contentPane.setPreferredSize(new Dimension(600, 400));
        init();
        textArea1.setEditable(false);
        textArea1.setText(msg);

    }

    public MessagePanel(@Nullable Project project, String msg) {
        super(project);
        this.project = project;
        setTitle("Success");
        contentPane.setPreferredSize(new Dimension(600, 400));
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
