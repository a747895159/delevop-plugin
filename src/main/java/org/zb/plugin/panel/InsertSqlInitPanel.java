package org.zb.plugin.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DocumentAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zb.plugin.restdoc.utils.ToolUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : ZhouBin
 */
public class InsertSqlInitPanel extends DialogWrapper {

    private JPanel contentPane;
    private JTextField columnInput;
    private JTextField valueInput;
    private JTextArea textArea1;

    /**
     * 项目对象
     */
    private Project project;


    public InsertSqlInitPanel(@Nullable Project project) {
        super(project);
        this.project = project;
        setTitle("InsertSql字段值初始化");
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

    /**
     * 确认按钮回调事件
     */
    private void onOK() {
        List<String> customColumns = parseFieldValues(columnInput.getText().trim());
        List<String> customValues = parseFieldValues(valueInput.getText().trim());
        if (customColumns.size() != customValues.size()) {
            Messages.showErrorDialog(project, "自定义列元素与值不匹配", "Information");
            return;
        }
        String sqlInputText = textArea1.getText().trim();
        StringBuilder sb = new StringBuilder();
        for (String sql : sqlInputText.split("\n")) {
            String s = singleSqlParser(sql, customColumns, customValues);
            sb.append(s).append("\n");
        }
        ToolUtil.writeClipboard(sb.toString(), project);
    }

    private static String singleSqlParser(String sql, java.util.List<String> customColumns, java.util.List<String> customValues) {
        String columnStr = sql.substring(sql.indexOf("(") + 1, sql.indexOf(")"));
        String valuesStr = sql.substring(sql.indexOf("(", sql.indexOf(")")) + 1, sql.lastIndexOf(")"));
        java.util.List<String> columnList = Arrays.stream(columnStr.split(",")).map(String::trim).collect(Collectors.toList());
        // 使用逗号和括号来解析字段值
        java.util.List<String> fieldValues = parseFieldValues(valuesStr);
        if (columnList.size() != fieldValues.size()) {
            throw new RuntimeException("sql解析异常");
        }
        String nowStr = "'" + DateFormatUtils.format(new Date(), "yyyy-MM-dd") + " 00:00:00.000000'";
        String userStr = "'0'";
        replaceVal(columnList, fieldValues, "created_at", nowStr);
        replaceVal(columnList, fieldValues, "created_by", userStr);
        replaceVal(columnList, fieldValues, "updated_at", nowStr);
        replaceVal(columnList, fieldValues, "updated_by", userStr);
        replaceVal(columnList, fieldValues, "last_updated_at", nowStr);
        if (customColumns != null) {
            for (int i = 0; i < customColumns.size(); i++) {
                String val = customValues.get(i);
                if (StringUtils.isNotBlank(val)) {
                    val = val.trim();
                    if (Objects.equals(val, "NULL") || Objects.equals(val, "null")) {
                        val = "NULL";
                    } else {
                        try {
                            Long.parseLong(val);
                        } catch (NumberFormatException e) {
                            val = "'" + val + "'";
                        }
                    }
                } else {
                    val = "'" + val + "'";
                }

                replaceVal(columnList, fieldValues, customColumns.get(i), val);
            }
        }
        return sql.substring(0, sql.indexOf("(", sql.indexOf(")")) + 1) +
                String.join(", ", fieldValues) + ");";
    }

    private static void replaceVal(java.util.List<String> columnList, java.util.List<String> fieldValues, String columnName, String value) {
        int i = columnList.indexOf(columnName);
        if (i == -1) {
            i = columnList.indexOf("`" + columnName + "`");
        }
        if (i != -1) {
            fieldValues.set(i, value);
        }
    }

    private static java.util.List<String> parseFieldValues(String valuesPart) {
        List<String> fieldValues = new ArrayList<>();
        StringBuilder fieldValue = new StringBuilder();
        boolean insideQuotes = false;
        int parenthesesLevel = 0;

        for (char c : valuesPart.toCharArray()) {
            if (c == '\'') {
                insideQuotes = !insideQuotes;
            } else if (c == '(' && !insideQuotes) {
                parenthesesLevel++;
            } else if (c == ')' && !insideQuotes) {
                parenthesesLevel--;
            }

            if (c == ',' && parenthesesLevel == 0 && !insideQuotes) {
                fieldValues.add(fieldValue.toString().trim());
                fieldValue.setLength(0);
            } else {
                fieldValue.append(c);
            }
        }

        fieldValues.add(fieldValue.toString().trim());

        return fieldValues;
    }
}
