package org.zb.plugin.restdoc.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import org.apache.commons.lang3.StringUtils;

public class JavaDocUtils {


    public static String getText(PsiDocComment comment) {
        if (comment == null) {
            return "";
        }
        PsiElement[] elements = comment.getDescriptionElements();
        return getTagText(elements);
    }

    public static String getTagText(PsiElement[] elements) {
        if (elements == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        boolean hasLastEnter = false;
        for (PsiElement psiElement : elements) {
            if (psiElement instanceof PsiDocToken) {
                PsiDocToken psiDocToken = (PsiDocToken) psiElement;
                stringBuffer.append(psiDocToken.getText() + "\n");
                hasLastEnter = true;
            }
        }
        if (hasLastEnter) {
            return convertDesc(stringBuffer.substring(0, stringBuffer.length() - 1));
        }
        return convertDesc(stringBuffer.toString());
    }

    public static String convertDesc(String content){
        if(StringUtils.isBlank(content)){
            return "";
        }
        StringBuilder sb =new StringBuilder();
        for(String s:content.split("\n")){
            String tmp = s.trim();
            if(StringUtils.isNotBlank(tmp)){
                sb.append(tmp).append("\n");
            }
        }
        return sb.toString().substring(0,sb.length()-1);
    }

    /**
     * 获得某个参数的描述
     * @param comment
     * @param paramName
     * @return
     */
    public static String getParamsDesc(PsiDocComment comment, String paramName) {
        if (comment == null) {
            return "";
        }
        PsiDocTag[] psiDocTags = comment.getTags();
        StringBuffer stringBuffer = new StringBuffer();
        for (PsiDocTag psiDocTag : psiDocTags) {
            if (psiDocTag.getText() == null || psiDocTag.getValueElement() == null || psiDocTag.getValueElement().getText() == null) {
                continue;
            }
            if (psiDocTag.getName().equals("param") && psiDocTag.getValueElement().getText().equals(paramName)) {
                int index = 0;
                for (PsiElement e : psiDocTag.getDataElements()) {
                    if (index++ == 0) {
                        continue;
                    }
                    stringBuffer.append(e.getText());
                }
                break;
            }
        }
        return stringBuffer.toString();
    }

}
