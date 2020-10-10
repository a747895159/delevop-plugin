package org.zb.plugin.restdoc.parser;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import org.zb.plugin.restdoc.utils.MyPsiSupport;

public abstract class Parser {

    abstract public void parseDefinition();

    public PsiType getRealType(PsiType psiType, PsiField psiField){
        PsiType fieldType = MyPsiSupport.getGenericsType(psiType, psiField);
        if (fieldType == null) {
            fieldType = psiField.getType();
        }
        return fieldType;
    }




}
