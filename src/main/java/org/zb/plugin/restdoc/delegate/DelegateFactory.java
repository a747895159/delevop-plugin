package org.zb.plugin.restdoc.delegate;

import org.zb.plugin.action.Bean2JsonAction;
import org.zb.plugin.action.RestControllerDocAction;
import org.zb.plugin.restdoc.delegate.impl.ClassSelectedDelegate;
import org.zb.plugin.restdoc.delegate.impl.ControllerDocumentDelegate;
import org.zb.plugin.restdoc.delegate.impl.DefaultDelegateFactory;

public class DelegateFactory {

    public static GeneratorDelegate getGeneratorDelegate(Class clazz) {
        if (clazz.equals(RestControllerDocAction.class)) {
            return new ControllerDocumentDelegate();
        }
        if (clazz.equals(Bean2JsonAction.class)) {
            return new ClassSelectedDelegate();
        }
        return new DefaultDelegateFactory();
    }

}
