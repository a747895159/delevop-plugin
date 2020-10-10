package org.zb.plugin.restdoc.checker.impl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.restdoc.checker.EventChecker;
import org.zb.plugin.restdoc.utils.MyPsiSupport;

public class ClassSelectedChecker implements EventChecker {

    @Override
    public boolean check(AnActionEvent event) {
        PsiJavaFile javaFile = MyPsiSupport.getPsiJavaFile(event);
        String selectedText = MyPsiSupport.getSelectedText(event);
        if(javaFile!=null){
            PsiClass[] psiClasses = javaFile.getClasses();
            for(PsiClass psiClass:psiClasses){
                if (StringUtils.equals(psiClass.getName(), selectedText)) {
                    return true;
                }
            }
        }
        return false;
    }
}
