package io.github.mthli.knife.history;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cauchywei on 16/1/9.
 */
public class TextChangedRecord {

    CharSequence changedText;
    int start;
    int length;
    List<SpanRecord> spanRecordList;

    public TextChangedRecord(Editable editable,CharSequence text, int start, int length, Class[] spanClasses) {
        changedText = text.subSequence(start,start+length);
        this.start = start;
        this.length = length;

        spanRecordList = new ArrayList<>();
        for (Class spanClass : spanClasses) {
            Object[] spans = editable.getSpans(start, start + length, spanClass);
            for (Object span : spans) {
                spanRecordList.add(new SpanRecord(span,editable.getSpanStart(span),editable.getSpanEnd(span)));
            }
        }
    }

    public CharSequence getChangedText() {
        return changedText;
    }
}
