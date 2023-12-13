package org.zb.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.zb.plugin.panel.Panel03;
import org.zb.plugin.putil.Fun03;
import org.zb.plugin.restdoc.utils.ToolUtil;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Insert sql 初始化
 *
 * @author : ZhouBin
 */
public class InsertSqlInitAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        Fun03<JTextField, JTextArea> fun03 = (columnInput, valueInput, textArea1) -> onOK(project, columnInput, valueInput, textArea1);
        Panel03 panel = new Panel03(project, fun03, "InsertSql字段值初始化", "自定义相关字段", "自定义列:", "自定义值:", "请输入Insert相关上去了脚本");
        panel.show();
    }


    /**
     * 确认按钮回调事件
     */
    private void onOK(Project project, JTextField columnInput, JTextField valueInput, JTextArea textArea1) {
        List<String> customColumns = parseFieldValues(columnInput.getText().trim());
        List<String> customValues = parseFieldValues(valueInput.getText().trim());
        if (customColumns.size() != customValues.size()) {
            Messages.showErrorDialog(project, "自定义列元素与值不匹配", "Information");
            return;
        }
        String sqlInputText = textArea1.getText().trim();
        StringBuilder sb = new StringBuilder();
        for (String sql : sqlInputText.split("\n")) {
            if (StringUtils.isNotBlank(sql)) {
                String s = singleSqlParser(sql, customColumns, customValues);
                sb.append(s).append("\n");
            }
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
