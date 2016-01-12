package io.github.mthli.knife.history.action;

import io.github.mthli.knife.history.SpanRecord;

/**
 * Created by cauchywei on 16/1/9.
 */
public class SpanReplacedAction extends SequentialAction {
    public SpanReplacedAction(int start, int end, Object spanBefore, Object spanAfter) {
        super(new SpanRemovedAction(new SpanRecord(spanBefore, start, end)), new SpanAddedAction(new SpanRecord(spanAfter, start, end)));
    }
}
