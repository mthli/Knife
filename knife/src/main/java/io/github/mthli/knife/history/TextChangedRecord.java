package io.github.mthli.knife.history;

import android.text.Editable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cauchywei on 16/1/9.
 */
public class TextChangedRecord {
    private CharSequence changedText;
    private int start;
    private int length;
    private List<SpanRecord> spanRecordList;

    public TextChangedRecord(Editable editable,CharSequence text, int start, int length, Class[] spanClasses) {
        this.changedText = text.subSequence(start, start + length);
        this.start = start;
        this.length = length;
        this.spanRecordList = new ArrayList<>();

        for (Class spanClass : spanClasses) {
            Object[] spans = editable.getSpans(start, start + length, spanClass);
            for (Object span : spans) {
                spanRecordList.add(new SpanRecord(span, editable.getSpanStart(span), editable.getSpanEnd(span)));
            }
        }
    }

    public CharSequence getChangedText() {
        return changedText;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public List<SpanRecord> getSpanRecordList() {
        return spanRecordList;
    }
}
