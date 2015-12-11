package io.github.mthli.knife;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.EditText;

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

    public static final int CONTAINS_ALGORITHM_AND = 0;
    public static final int CONTAINS_ALGORITHM_OR = 1;

    private int containsAlgorithm = CONTAINS_ALGORITHM_OR;
    private int spannedFlags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    public KnifeText(Context context) {
        super(context);
        init(null);
    }

    public KnifeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public KnifeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressWarnings("NewApi")
    public KnifeText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // TODO
    }

    public void setContainsAlgorithm(int flags) {
        if (flags == CONTAINS_ALGORITHM_AND) {
            this.containsAlgorithm = CONTAINS_ALGORITHM_AND;
        } else if (flags == CONTAINS_ALGORITHM_OR) {
            this.containsAlgorithm = CONTAINS_ALGORITHM_OR;
        } else {
            throw new IllegalArgumentException("flags must be KnifeText.CONTAINS_ALGORITHM_AND or KnifeText.CONTAINS_ALGORITHM_OR");
        }
    }

    public void setSpannedFlags(int flags) {
        if (flags == Spanned.SPAN_INCLUSIVE_EXCLUSIVE) {
            this.spannedFlags = Spanned.SPAN_INCLUSIVE_EXCLUSIVE;
        } else if (flags == Spanned.SPAN_INCLUSIVE_INCLUSIVE) {
            this.spannedFlags = Spanned.SPAN_INCLUSIVE_INCLUSIVE;
        } else if (flags == Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) {
            this.spannedFlags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        } else if (flags == Spanned.SPAN_EXCLUSIVE_INCLUSIVE) {
            this.spannedFlags = Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
        } else {
            throw new IllegalArgumentException(
                    "flags must be one of Spanned.SPAN_INCLUSIVE_EXCLUSIVE, "
                            + "Spanned.SPAN_INCLUSIVE_INCLUSIVE, "
                            + "Spanned.SPAN_EXCLUSIVE_EXCLUSIVE, "
                            + "Spanned.SPAN_EXCLUSIVE_INCLUSIVE"
            );
        }

        // TODO change all span
    }

    public boolean contains(int format)
    {
        switch (format) {
            case FORMAT_BOLD:
                return containBold();
            case FORMAT_ITALIC:
                return containItalic();
            case FORMAT_UNDERLINED:
                return containUnderlined();
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

    // TODO
    private boolean containBold() {
        int start = getSelectionStart();
        int end = getSelectionEnd();

        if (start == end) {
            if (start - 1 < 0 || start + 1 > getEditableText().length()) {
                return false;
            } else {
                StyleSpan[] before = getEditableText().getSpans(start - 1, start, StyleSpan.class);
                StyleSpan[] after = getEditableText().getSpans(start, start + 1, StyleSpan.class);
                if (before.length > 0 && after.length > 0 && before[0].getStyle() == Typeface.BOLD && after[0].getStyle() == Typeface.BOLD) {
                    return true;
                }
            }
        } else {
            StyleSpan[] spans = getEditableText().getSpans(start, end, StyleSpan.class);
            for (StyleSpan span : spans) {
                if (span.getStyle() == Typeface.BOLD) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean containItalic() {
        return false;
    }

    private boolean containUnderlined() {
        return false;
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

    public void bold(boolean valid) {
        if (valid) {
            boldValid();
        } else {
            boldInvalid();
        }
    }

    private void boldValid() {
        getEditableText().setSpan(new StyleSpan(Typeface.BOLD), getSelectionStart(), getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void boldInvalid() {
        Editable editable = getEditableText();
        StyleSpan[] spans = editable.getSpans(getSelectionStart(), getSelectionEnd(), StyleSpan.class);

        for (StyleSpan span : spans) {
            if (span.getStyle() == Typeface.BOLD) {
                int start = editable.getSpanStart(span);
                int end = editable.getSpanEnd(span);
                editable.setSpan(new StyleSpan(Typeface.NORMAL), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
