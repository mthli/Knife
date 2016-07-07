package io.github.mthli.knife.history.action;

import android.text.Editable;
import android.text.Spanned;

import io.github.mthli.knife.history.SpanRecord;

/**
 * Created by cauchywei on 16/1/9.
 */
public class SpanRemovedAction implements Action{
    private SpanRecord spanRecord;

    public SpanRemovedAction(SpanRecord spanRecord) {
        this.spanRecord = spanRecord;
    }

    public SpanRemovedAction(Object span, int start, int end) {
        this.spanRecord = new SpanRecord(span, start, end);
    }

    @Override
    public void undo(Editable editable) {
        editable.setSpan(spanRecord.getSpan(), spanRecord.getStart(), spanRecord.getEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void redo(Editable editable) {
        editable.removeSpan(spanRecord.getSpan());
    }

    @Override
    public void onRemoved() {}
}
