package io.github.mthli.knife;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;
import android.text.style.UpdateLayout;

import org.xml.sax.Attributes;

/**
 * Created by Onko on 7/1/2016.
 */

public class CommentSpan extends CharacterStyle {

    private String text;
    private int width;

    public CommentSpan(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void updateDrawState(TextPaint tp) {

    }
}
