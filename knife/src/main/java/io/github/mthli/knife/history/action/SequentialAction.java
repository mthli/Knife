package io.github.mthli.knife.history.action;

import android.text.Editable;

/**
 * Created by cauchywei on 16/1/9.
 */
public class SequentialAction implements Action {

    Action[] actions;

    public SequentialAction(Action ...actions) {
        this.actions = actions;
    }

    @Override
    public void undo(Editable editable) {
        for (int i = actions.length - 1; i >= 0; i--) {
            actions[i].undo(editable);
        }
    }

    @Override
    public void redo(Editable editable) {
        for (int i = 0; i < actions.length; i++) {
            actions[i].redo(editable);
        }
    }

    @Override
    public void onRemoved() {

    }
}
