package org.zb.plugin.restdoc.checker.impl;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.restdoc.checker.EventChecker;
import org.zb.plugin.restdoc.constant.SpringConstant;
import org.zb.plugin.restdoc.utils.MyPsiSupport;

import java.util.List;

public class SpringControllerChecker implements EventChecker {

    @Override
    public boolean check(AnActionEvent event) {
        PsiJavaFile psiJavaFile = MyPsiSupport.getPsiJavaFile(event);
        if(psiJavaFile == null){
            return false;
        }
        String selectedText = MyPsiSupport.getSelectedText(event);
        List<PsiClass> targetClassList = MyPsiSupport.getPsiClasses(psiJavaFile, SpringConstant.ANNOTATION_RESCONTROLLER);
        //选中的类名
        for (PsiClass psiClass : targetClassList) {
            if (StringUtils.equals(psiClass.getName(), selectedText)) {
                return true;
            }
        }
        //选中的方法名
        for (PsiMethod method : MyPsiSupport.getAllRequestPsiMethod(targetClassList)) {
            if (StringUtils.equals(method.getName(), selectedText)) {
               return true;
            }
        }
        return false;
    }
}
