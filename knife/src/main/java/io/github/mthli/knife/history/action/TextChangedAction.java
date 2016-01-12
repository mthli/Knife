package io.github.mthli.knife.history.action;

import android.text.Editable;
import android.text.Spanned;

import io.github.mthli.knife.history.SpanRecord;
import io.github.mthli.knife.history.TextChangedRecord;

/**
 * Created by cauchywei on 16/1/9.
 */
public class TextChangedAction implements Action {
    private TextChangedRecord before;
    private TextChangedRecord after;

    public TextChangedAction(TextChangedRecord before, TextChangedRecord after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public void undo(Editable editable) {
        editable.replace(after.getStart(), after.getStart() + after.getChangedText().length(), before.getChangedText());
        for (SpanRecord beforeSpanRecord : before.getSpanRecordList()) {
            editable.setSpan(beforeSpanRecord.getSpan(), beforeSpanRecord.getStart(), beforeSpanRecord.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void redo(Editable editable) {
        editable.replace(before.getStart(), before.getStart() + before.getChangedText().length(), after.getChangedText());
        for (SpanRecord afterSpan : after.getSpanRecordList()) {
            editable.setSpan(afterSpan.getSpan(), afterSpan.getStart(), afterSpan.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void onRemoved() {}
}
