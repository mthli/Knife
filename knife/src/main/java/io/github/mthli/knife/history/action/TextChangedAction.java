package io.github.mthli.knife.history.action;

import android.text.Editable;
import android.text.Spanned;
import android.util.Log;

import io.github.mthli.knife.history.SpanRecord;
import io.github.mthli.knife.history.TextChangedRecord;

/**
 * Created by cauchywei on 16/1/9.
 */
public class TextChangedAction implements Action {

    TextChangedRecord before;
    TextChangedRecord after;

    public TextChangedAction(TextChangedRecord before, TextChangedRecord after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public void undo(Editable editable) {
        editable.replace(after.start,after.start+after.changedText.length(),before.getChangedText());
        for (SpanRecord beforeSpanRecord : before.spanRecordList) {
            editable.setSpan(beforeSpanRecord.span,beforeSpanRecord.start,beforeSpanRecord.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void redo(Editable editable) {
        editable.replace(before.start,before.start+before.changedText.length(),after.getChangedText());
        for (SpanRecord afterSpan : after.spanRecordList) {
            editable.setSpan(afterSpan.span,afterSpan.start,afterSpan.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void onRemoved() {
    }
}
