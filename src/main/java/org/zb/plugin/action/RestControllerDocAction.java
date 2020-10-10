package org.zb.plugin.action;

import com.google.common.collect.Lists;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import io.netty.util.internal.ThrowableUtil;
import org.apache.commons.lang3.StringUtils;
import org.zb.plugin.restdoc.constant.SpringConstant;
import org.zb.plugin.restdoc.definition.RestFulDefinition;
import org.zb.plugin.restdoc.delegate.DelegateFactory;
import org.zb.plugin.restdoc.delegate.GeneratorDelegate;
import org.zb.plugin.restdoc.generator.RestDocumentGenerator;
import org.zb.plugin.restdoc.parser.RestParser;
import org.zb.plugin.restdoc.utils.MyPsiSupport;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring controller 转换为markdown 文档
 *
 * @author :  ZhouBin
 * @date :  2019-10-23
 */
public class RestControllerDocAction extends AnAction {

    private static Project project;

    private String selectedText;

    private GeneratorDelegate delegate;

    private GeneratorDelegate getDelegate() {
        if (delegate == null) {
            delegate = DelegateFactory.getGeneratorDelegate(this.getClass());
        }
        return delegate;
    }


    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        ToolUtil.LOGGER.info("test-1  {}", "11");

        ToolUtil.LOGGER.error("test-1  {}", "11AA");

        System.out.println("111111");


        Editor editor = anActionEvent.getDataContext().getData(CommonDataKeys.EDITOR);
        selectedText = MyPsiSupport.getSelectedText(anActionEvent);
        if (StringUtils.isBlank(selectedText)) {
            return;
        }
        project = editor.getProject();
        StringBuilder sb = new StringBuilder();
        try {
            List<RestFulDefinition> definitionList = parseSelected(anActionEvent);
            if (definitionList.size() != 0) {
                definitionList.forEach(definition -> {
                    RestDocumentGenerator generator = new RestDocumentGenerator(definition);
                    sb.append(generator.generate()).append("\n");
                });
                ToolUtil.writeClipboard(sb.toString(), project, selectedText, "markdown doc");
            } else {
                ToolUtil.notifyMsg("please selected RestController className or methodName.", NotificationType.WARNING, project);
            }
        } catch (Exception e) {
            ToolUtil.notifyMsg("RestController to doc failed.\n" + ThrowableUtil.stackTraceToString(e), NotificationType.ERROR, project);
            ToolUtil.LOGGER.error("RestController to doc failed.\n", e);
        }
        ToolUtil.LOGGER.info("test-1  {}", "a");

        ToolUtil.LOGGER.error("test-1 {}", "b");

        project = null;

    }


    private List<RestFulDefinition> parseSelected(AnActionEvent anActionEvent) {
        PsiJavaFile javaFile = MyPsiSupport.getPsiJavaFile(anActionEvent);
        //获得相应带有@RestController 的类
        List<PsiClass> targetClassList = MyPsiSupport.getPsiClasses(javaFile, SpringConstant.ANNOTATION_RESCONTROLLER);
        PsiClass selectedClass = null;
        for (PsiClass psiClass : targetClassList) {
            if (StringUtils.equals(psiClass.getName(), selectedText)) {
                selectedClass = psiClass;

                break;
            }
        }
        List<PsiMethod> parseMethod = new ArrayList<>();
        if (selectedClass != null) {
            parseMethod.addAll(MyPsiSupport.getAllRequestPsiMethod(Lists.newArrayList(selectedClass)));
        } else {
            for (PsiMethod method : MyPsiSupport.getAllRequestPsiMethod(targetClassList)) {
                if (StringUtils.equals(method.getName(), selectedText)) {
                    parseMethod.add(method);
                    break;
                }
            }
        }
        List<RestFulDefinition> list = new ArrayList<>(parseMethod.size());
        if (parseMethod.size() != 0) {
            parseMethod.forEach(psiMethod -> {
                RestParser parser = new RestParser(psiMethod);
                parser.parseDefinition();
                list.add(parser.getDefinition());
            });
        }
        return list;
    }


    @Override
    public boolean isDumbAware() {
        return false;
    }


    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        this.getDelegate().doUpdate(e);
    }


}
