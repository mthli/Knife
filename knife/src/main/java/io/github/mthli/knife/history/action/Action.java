package io.github.mthli.knife.history.action;

import android.text.Editable;

/**
 * Created by cauchywei on 16/1/9.
 */
public interface Action {
    void undo(Editable editable);

    void redo(Editable editable);

    void onRemove();
}
