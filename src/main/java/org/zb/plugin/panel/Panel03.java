package org.zb.plugin.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DocumentAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zb.plugin.putil.Fun03;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

/**
 * @author : ZhouBin
 */
public class Panel03 extends DialogWrapper {

    private JPanel contentPane;
    private JTextField columnInput;
    private JTextField valueInput;
    private JTextArea textArea1;

    private Fun03<JTextField, JTextArea> supplier;

    /**
     * 项目对象
     */
    private Project project;


    public Panel03(@Nullable Project project, Fun03<JTextField, JTextArea> fun03,String title) {
        super(project);
        this.project = project;
        this.supplier = fun03;
        setTitle(title);
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
        try {
            supplier.execute(columnInput, valueInput, textArea1);
        } catch (Exception e) {
            String message = e.getMessage();
            message = message == null ? e.toString() : message;
            Messages.showErrorDialog(project, message, "Information");
        }
        super.doOKAction();
    }

    private void initEvent() {

        this.getOKAction().setEnabled(false);
        DocumentAdapter adapter = new DocumentAdapter() {
            @Override
            public void textChanged(@NotNull DocumentEvent event) {
                enableOkAction();
            }
        };
        this.textArea1.getDocument().addDocumentListener(adapter);
    }

    private void enableOkAction() {
        String text = this.textArea1.getText().trim();
        this.getOKAction().setEnabled(false);
        if (StringUtils.isNotBlank(text)) {
            this.getOKAction().setEnabled(true);
        }
    }

    private void initPanel() {

    }


}
