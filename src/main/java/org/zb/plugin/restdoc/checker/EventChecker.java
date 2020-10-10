package org.zb.plugin.restdoc.checker;

import com.intellij.openapi.actionSystem.AnActionEvent;

public interface EventChecker {

    boolean check(AnActionEvent event);

}
