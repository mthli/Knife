package io.github.mthli.knife;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class KnifeText extends EditText {
    public static final int FORMAT_BOLD = 0x01;
    public static final int FORMAT_ITALIC = 0x02;
    public static final int FORMAT_UNDERLINED = 0x03;
    public static final int FORMAT_STRIKETHROUGH = 0x04;
    public static final int FORMAT_LIST_BULLETED = 0x05;
    public static final int FORMAT_LIST_NUMBERED = 0x06;
    public static final int FORMAT_QUOTE = 0x07;
    public static final int FORMAT_LINK = 0x08;
    public static final int FORMAT_IMAGE = 0x09;

    public KnifeText(Context context) {
        super(context);
    }

    public KnifeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KnifeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("NewApi")
    public KnifeText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Contains ====================================================================================

    public boolean contains(int format) {
        switch (format) {
            case FORMAT_BOLD:
                return containStyle(Typeface.BOLD, getSelectionStart(), getSelectionEnd());
            case FORMAT_ITALIC:
                return containStyle(Typeface.ITALIC, getSelectionStart(), getSelectionEnd());
            case FORMAT_UNDERLINED:
                return containUnderline(getSelectionStart(), getSelectionEnd());
            case FORMAT_STRIKETHROUGH:
                return containStrikethrough();
            case FORMAT_LIST_BULLETED:
                return containListBulleted();
            case FORMAT_LIST_NUMBERED:
                return containListNumbered();
            case FORMAT_QUOTE:
                return containQuote();
            case FORMAT_LINK:
                return containLink();
            case FORMAT_IMAGE:
                return containImage();
            default:
                return false;
        }
    }

    private boolean containStrikethrough() {
        return false;
    }

    private boolean containListBulleted() {
        return false;
    }

    private boolean containListNumbered() {
        return false;
    }

    private boolean containQuote() {
        return false;
    }

    private boolean containLink() {
        return false;
    }

    private boolean containImage() {
        return false;
    }

    // StyleSpan ===================================================================================

    public void bold(boolean valid) {
        if (valid) {
            styleValid(Typeface.BOLD, getSelectionStart(), getSelectionEnd());
        } else {
            styleInvalid(Typeface.BOLD, getSelectionStart(), getSelectionEnd());
        }
    }

    public void italic(boolean valid) {
        if (valid) {
            styleValid(Typeface.ITALIC, getSelectionStart(), getSelectionEnd());
        } else {
            styleInvalid(Typeface.ITALIC, getSelectionStart(), getSelectionEnd());
        }
    }

    private void styleValid(int style, int start, int end) {
        switch (style) {
            case Typeface.NORMAL:
            case Typeface.BOLD:
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                break;
            default:
                return;
        }

        if (start >= end) {
            return;
        }

        getEditableText().setSpan(new StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void styleInvalid(int style, int start, int end) {
        switch (style) {
            case Typeface.NORMAL:
            case Typeface.BOLD:
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                break;
            default:
                return;
        }

        if (start >= end) {
            return;
        }

        StyleSpan[] spans = getEditableText().getSpans(start, end, StyleSpan.class);
        List<Part> list = new ArrayList<>();

        for (StyleSpan span : spans) {
            if (span.getStyle() == style) {
                list.add(new Part(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span)));
                getEditableText().removeSpan(span);
            }
        }

        for (Part part : list) {
            if (part.isValid()) {
                if (part.getStart() < start) {
                    styleValid(style, part.getStart(), start);
                }

                if (part.getEnd() > end) {
                    styleValid(style, end, part.getEnd());
                }
            }
        }
    }

    private boolean containStyle(int style, int start, int end) {
        switch (style) {
            case Typeface.NORMAL:
            case Typeface.BOLD:
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                break;
            default:
                return false;
        }

        if (start > end) {
            return false;
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > getEditableText().length()) {
                return false;
            } else {
                StyleSpan[] before = getEditableText().getSpans(start - 1, start, StyleSpan.class);
                StyleSpan[] after = getEditableText().getSpans(start, start + 1, StyleSpan.class);
                return before.length > 0 && after.length > 0 && before[0].getStyle() == style && after[0].getStyle() == style;
            }
        } else {
            StringBuilder builder = new StringBuilder();

            // Make sure no duplicate characters be added
            for (int i = start; i < end; i++) {
                StyleSpan[] spans = getEditableText().getSpans(i, i + 1, StyleSpan.class);
                for (StyleSpan span : spans) {
                    if (span.getStyle() == style) {
                        builder.append(getEditableText().subSequence(i, i + 1).toString());
                        break;
                    }
                }
            }

            return getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
    }

    // UnderlineSpan ===============================================================================

    public void underline(boolean valid) {
        if (valid) {
            underlineValid(getSelectionStart(), getSelectionEnd());
        } else {
            underlineInvalid(getSelectionStart(), getSelectionEnd());
        }
    }

    private void underlineValid(int start, int end) {
        if (start >= end) {
            return;
        }

        getEditableText().setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void underlineInvalid(int start, int end) {
        if (start >= end) {
            return;
        }

        UnderlineSpan[] spans = getEditableText().getSpans(start, end, UnderlineSpan.class);
        List<Part> list = new ArrayList<>();

        for (UnderlineSpan span : spans) {
            list.add(new Part(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span)));
            getEditableText().removeSpan(span);
        }

        for (Part part : list) {
            if (part.isValid()) {
                if (part.getStart() < start) {
                    underlineValid(part.getStart(), start);
                }

                if (part.getEnd() > end) {
                    underlineValid(end, part.getEnd());
                }
            }
        }
    }

    private boolean containUnderline(int start, int end) {
        if (start > end) {
            return false;
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > getEditableText().length()) {
                return false;
            } else {
                UnderlineSpan[] before = getEditableText().getSpans(start - 1, start, UnderlineSpan.class);
                UnderlineSpan[] after = getEditableText().getSpans(start, start + 1, UnderlineSpan.class);
                return before.length > 0 && after.length > 0;
            }
        } else {
            StringBuilder builder = new StringBuilder();

            // Make sure no duplicate characters be added
            for (int i = start; i < end; i++) {
                if (getEditableText().getSpans(i, i + 1, UnderlineSpan.class).length > 0) {
                    builder.append(getEditableText().subSequence(i, i + 1).toString());
                }
            }

            return getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
    }
}
