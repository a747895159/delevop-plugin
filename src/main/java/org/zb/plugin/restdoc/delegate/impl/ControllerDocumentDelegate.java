package org.zb.plugin.restdoc.delegate.impl;

import org.zb.plugin.restdoc.checker.impl.EditorAvailableChecker;
import org.zb.plugin.restdoc.checker.impl.JavaFileChecker;
import org.zb.plugin.restdoc.checker.impl.SpringControllerChecker;
import org.zb.plugin.restdoc.delegate.GeneratorDelegate;

public class ControllerDocumentDelegate extends GeneratorDelegate {

    public ControllerDocumentDelegate(){
        this.addChecker(new EditorAvailableChecker());
        this.addChecker(new JavaFileChecker());
        this.addChecker(new SpringControllerChecker());
    }

}
