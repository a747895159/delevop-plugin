package org.zb.plugin.restdoc.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zb.plugin.restdoc.view.DocumentExportDialog;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author :  ZhouBin
 * @date :  2019-10-22
 */
public class ToolUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ToolUtil.class);

    private static Gson gson = new Gson();
    private static NotificationGroup notificationGroup = new NotificationGroup("Java2Json.NotificationGroup", NotificationDisplayType.BALLOON, true);

    public static void notifyMsg(String message, NotificationType type, Project project) {
        Notification success = notificationGroup.createNotification(message, type);
        Notifications.Bus.notify(success, project);
    }

    public static String clearDesc(String docStr) {
        if (docStr != null && docStr.length() > 0) {
            docStr = docStr.replace("/**", "");
            docStr = docStr.replace("*/", "");
            docStr = docStr.replace("*", "");
            docStr = docStr.replace("\n", "");
            docStr = docStr.replace("//", "");
            docStr = docStr.trim();
        }
        return docStr;
    }

    public static void writeClipboard(String content, Project project, String selectName, String formatName) {
        StringSelection selection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        String message = "Convert " + selectName + " to " + formatName + " success, copied to clipboard.";
        notifyMsg(message, NotificationType.INFORMATION, project);
    }
    public static void writeClipboard(String content, Project project) {
        StringSelection selection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        String message = "convert  success,already copied to clipboard.";
        notifyMsg(message, NotificationType.INFORMATION, project);
    }

    public static String toStr(Object object) {
        if (object != null) {
            return gson.toJson(object);
        }
        return null;
    }

    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return new GsonBuilder().create().toJson(object);
    }


    public static void logInfo(String desc, Object... values) {
        StringBuilder sb = new StringBuilder("----   " + desc + " ----    ");
        for (Object v : values) {
            if (v != null) {
                sb.append(v.toString()).append(" ++ ");
            }
        }
        LOGGER.info(sb.toString().substring(0, sb.length() - 4));
    }

    /**
     * 打开结果对话框
     *
     * @param result
     */
    public static void openDialog(String result) {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (int) (screensize.width * 0.3);
        int h = (int) (screensize.height * 0.3);

        DocumentExportDialog dialog = new DocumentExportDialog(result);
        dialog.setSize(w, h);
        dialog.pack();
        dialog.setLocation((int) (screensize.width * 0.5) - (int) (w * 0.5), (int) (screensize.height * 0.5) - (int) (h * 0.5));
        dialog.setVisible(true);

    }

}
