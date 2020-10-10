package org.zb.plugin.restdoc.delegate.impl;

import org.zb.plugin.restdoc.checker.impl.ClassSelectedChecker;
import org.zb.plugin.restdoc.checker.impl.EditorAvailableChecker;
import org.zb.plugin.restdoc.checker.impl.JavaFileChecker;
import org.zb.plugin.restdoc.delegate.GeneratorDelegate;

/**
 * @author :  ZhouBin
 * @date :  2019-11-04
 */
public class ClassSelectedDelegate extends GeneratorDelegate {

    public ClassSelectedDelegate() {
        this.addChecker(new EditorAvailableChecker());
        this.addChecker(new JavaFileChecker());
        this.addChecker(new ClassSelectedChecker());
    }
}
