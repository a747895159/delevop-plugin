package org.zb.plugin.group.extconfig;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zb.plugin.restdoc.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : ZhouBin
 */
public class ExtConfigGroup extends ActionGroup {

    public List<AnAction> anActionList = null;

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        AnAction[] anActions = findAnActions();
        ToolUtil.LOGGER.info("ExtConfigGroup sub   {}", anActions.length);
        return anActions;
    }

    public AnAction[] findAnActions() {
        if (anActionList == null) {
            ActionManager actionManager = ActionManager.getInstance();
            register(new Test001Action(), actionManager);
            register(new Test002Action(), actionManager);
            register(new Test003Action(), actionManager);
        }
        return anActionList.toArray(new AnAction[0]);
    }

    private void register(AnAction anAction, ActionManager actionManager) {
        if (anActionList == null) {
            anActionList = new ArrayList<>();
        }
        actionManager.registerAction(anAction.getClass().getName() + "__20231219", anAction);
        anActionList.add(anAction);
    }
}
