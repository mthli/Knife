package io.github.mthli.knife.history;

import android.os.Parcel;
import android.text.ParcelableSpan;

/**
 * Created by cauchywei on 16/1/9.
 */
public class SpanRecord {
    Object span;
    int start;
    int end;

    public SpanRecord(Object span, int start, int end) {
        this.span = span;
        this.start = start;
        this.end = end;
    }

    public void recycle() {
//        if (spanParcel != null) {
//            spanParcel.recycle();
//        }
    }
}
