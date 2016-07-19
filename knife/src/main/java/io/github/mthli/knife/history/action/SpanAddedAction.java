package io.github.mthli.knife.history.action;

import android.text.Editable;
import android.text.Spanned;

import io.github.mthli.knife.KnifeURLSpan;
import io.github.mthli.knife.history.SpanRecord;

/**
 * Created by cauchywei on 16/1/9.
 */
public class SpanAddedAction implements Action{

    private SpanRecord spanRecord;

    public SpanAddedAction(SpanRecord spanRecord) {
        this.spanRecord = spanRecord;
    }

    public SpanAddedAction(Object span, int start, int end) {
        this.spanRecord = new SpanRecord(span, start, end);
    }

    @Override
    public void undo(Editable editable) {
        editable.removeSpan(spanRecord.span);
    }

    @Override
    public void redo(Editable editable) {
        editable.setSpan(spanRecord.span,spanRecord.start,spanRecord.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onRemoved() {

    }
}
