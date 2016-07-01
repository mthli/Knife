package io.github.mthli.knife;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;

/**
 * Created by Onko on 7/1/2016.
 */

public class UnknownHtmlSpan extends ReplacementSpan implements ParagraphStyle {

    private String tag;
    private CharSequence text;
    private int width;

    public UnknownHtmlSpan(String tag) {
        this.tag = tag;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //return text with relative to the Paint
        this.text = text.subSequence(start, end);
        width = (int) paint.measureText(text, start, end);
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //draw the frame with custom Paint
        canvas.drawRect(x, top, x + width, bottom, paint);
    }

    public String getSource() {
        return "<" + tag + ">" + text + "</" + tag + ">";
    }
}
