package io.github.mthli.knife.history.action;

import android.text.Editable;

/**
 * Created by cauchywei on 16/1/9.
 */
public class TextReplacedAction implements Action {

    private int start;
    private CharSequence before,after;

    public TextReplacedAction(int start, CharSequence before, CharSequence after) {
        this.start = start;
        this.before = before;
        this.after = after;
    }

    @Override
    public void undo(Editable editable) {
        editable.replace(start,start+after.length(),before);
    }

    @Override
    public void redo(Editable editable) {
        editable.replace(start,start+before.length(),after);
    }

    @Override
    public void onRemoved() {

    }
}
