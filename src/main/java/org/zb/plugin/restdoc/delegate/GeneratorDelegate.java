package org.zb.plugin.restdoc.delegate;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.zb.plugin.restdoc.checker.EventChecker;

import java.util.HashSet;
import java.util.Set;


/**
 * 主要作为生产类的委托类
 *
 */
public abstract class GeneratorDelegate {

    private Set<EventChecker> checkers = new HashSet<>();

    public GeneratorDelegate addChecker(EventChecker checker) {
        this.checkers.add(checker);
        return this;
    }

    public void doUpdate(AnActionEvent event) {
        for (EventChecker checker : checkers) {
            boolean result = checker.check(event);

            if (!result) {
                event.getPresentation().setEnabled(false);
                break;
            }
        }
    }


}
