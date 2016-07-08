package io.github.mthli.knife;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spanned;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;

import org.xml.sax.Attributes;

/**
 * Created by Onko on 7/1/2016.
 */

public class UnknownHtmlSpan extends ReplacementSpan implements ParagraphStyle {

    public static final String TEXT = "Unknown";

    private String tag;
    private Attributes attributes;
    private Spanned spanned;
    private int width;

    public UnknownHtmlSpan(String tag, Attributes attributes, Spanned spanned) {
        this.tag = tag;
        this.attributes = attributes;
        this.spanned = spanned;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //return text with relative to the Paint
        width = (int) paint.measureText(text, start, end);
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //draw the frame with custom Paint
        canvas.drawRect(x, top, x + width, bottom, paint);
    }

    public Spanned getContent() {
        return spanned;
    }

    public String getStartTag() {
        if (attributes.getLength() != 0) {
            // TODO: add attributes back
        }
        return "<" + tag + ">";
    }

    public String getEndTag() {
        return "</" + tag + ">";
    }

    public String getTagName() {
        return tag;
    }
}
