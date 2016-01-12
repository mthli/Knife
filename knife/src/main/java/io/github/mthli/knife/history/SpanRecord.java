package io.github.mthli.knife.history;

/**
 * Created by cauchywei on 16/1/9.
 */
public class SpanRecord {
    private Object span;
    private int start;
    private int end;

    public SpanRecord(Object span, int start, int end) {
        this.span = span;
        this.start = start;
        this.end = end;
    }

    public Object getSpan() {
        return span;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
