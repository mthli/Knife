package io.github.mthli.knife;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class KnifeText extends EditText {
    public static final int FORMAT_BOLD = 0x01;
    public static final int FORMAT_ITALIC = 0x02;
    public static final int FORMAT_UNDERLINED = 0x03;
    public static final int FORMAT_STRIKETHROUGH = 0x04;
    public static final int FORMAT_BULLET = 0x05;
    public static final int FORMAT_QUOTE = 0x06;
    public static final int FORMAT_LINK = 0x07;
    public static final int FORMAT_IMAGE = 0x08;

    private int bulletColor = -1;
    private int bulletGapWidth = -1;
    private int linkColor = -1;
    private boolean linkUnderline = true;
    private int quoteColor = -1;
    private int quoteGapWidth = -1;
    private int strikethroughColor = -1;
    private int underlineColor = -1;

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
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.KnifeText);
        bulletColor = array.getColor(R.styleable.KnifeText_bulletColor, -1);
        bulletGapWidth = array.getInt(R.styleable.KnifeText_bulletGapWidth, -1);
        linkColor = array.getColor(R.styleable.KnifeText_linkColor, -1);
        linkUnderline = array.getBoolean(R.styleable.KnifeText_linkUnderline, true);
        quoteColor = array.getColor(R.styleable.KnifeText_quoteColor, -1);
        quoteGapWidth = array.getInt(R.styleable.KnifeText_quoteCapWidth, -1);
        strikethroughColor = array.getColor(R.styleable.KnifeText_strikethroughColor, -1);
        underlineColor = array.getColor(R.styleable.KnifeText_underlineColor, -1);
        array.recycle();
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
                return containStrikethrough(getSelectionStart(), getSelectionEnd());
            case FORMAT_BULLET:
                return containBullet();
            case FORMAT_QUOTE:
                return containQuote();
            case FORMAT_LINK:
                return containLink(getSelectionStart(), getSelectionEnd());
            case FORMAT_IMAGE:
                return containImage();
            default:
                return false;
        }
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

    protected void styleValid(int style, int start, int end) {
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

    protected void styleInvalid(int style, int start, int end) {
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
        List<KnifePart> list = new ArrayList<>();

        for (StyleSpan span : spans) {
            if (span.getStyle() == style) {
                list.add(new KnifePart(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span)));
                getEditableText().removeSpan(span);
            }
        }

        for (KnifePart part : list) {
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

    protected boolean containStyle(int style, int start, int end) {
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

    protected void underlineValid(int start, int end) {
        if (start >= end) {
            return;
        }

        getEditableText().setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    protected void underlineInvalid(int start, int end) {
        if (start >= end) {
            return;
        }

        UnderlineSpan[] spans = getEditableText().getSpans(start, end, UnderlineSpan.class);
        List<KnifePart> list = new ArrayList<>();

        for (UnderlineSpan span : spans) {
            list.add(new KnifePart(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span)));
            getEditableText().removeSpan(span);
        }

        for (KnifePart part : list) {
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

    protected boolean containUnderline(int start, int end) {
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

            for (int i = start; i < end; i++) {
                if (getEditableText().getSpans(i, i + 1, UnderlineSpan.class).length > 0) {
                    builder.append(getEditableText().subSequence(i, i + 1).toString());
                }
            }

            return getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
    }

    // StrikethroughSpan ===========================================================================

    public void strikethrough(boolean valid) {
        if (valid) {
            strikethroughValid(getSelectionStart(), getSelectionEnd());
        } else {
            strikethroughInvalid(getSelectionStart(), getSelectionEnd());
        }
    }

    protected void strikethroughValid(int start, int end) {
        if (start >= end) {
            return;
        }

        getEditableText().setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    protected void strikethroughInvalid(int start, int end) {
        if (start >= end) {
            return;
        }

        StrikethroughSpan[] spans = getEditableText().getSpans(start, end, StrikethroughSpan.class);
        List<KnifePart> list = new ArrayList<>();

        for (StrikethroughSpan span : spans) {
            list.add(new KnifePart(getEditableText().getSpanStart(span), getEditableText().getSpanEnd(span)));
            getEditableText().removeSpan(span);
        }

        for (KnifePart part : list) {
            if (part.isValid()) {
                if (part.getStart() < start) {
                    strikethroughValid(part.getStart(), start);
                }

                if (part.getEnd() > end) {
                    strikethroughValid(end, part.getEnd());
                }
            }
        }
    }

    protected boolean containStrikethrough(int start, int end) {
        if (start > end) {
            return false;
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > getEditableText().length()) {
                return false;
            } else {
                StrikethroughSpan[] before = getEditableText().getSpans(start - 1, start, StrikethroughSpan.class);
                StrikethroughSpan[] after = getEditableText().getSpans(start, start + 1, StrikethroughSpan.class);
                return before.length > 0 && after.length > 0;
            }
        } else {
            StringBuilder builder = new StringBuilder();

            for (int i = start; i < end; i++) {
                if (getEditableText().getSpans(i, i + 1, StrikethroughSpan.class).length > 0) {
                    builder.append(getEditableText().subSequence(i, i + 1).toString());
                }
            }

            return getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
    }

    // BulletSpan ==================================================================================

    public void bullet(boolean valid) {
        if (valid) {
            bulletValid();
        } else {
            bulletInvalid();
        }
    }

    protected void bulletValid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");

        for (int i = 0; i < lines.length; i++) {
            if (containBullet(i)) {
                continue;
            }

            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1; // \n
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            // Find selection area inside
            int bulletStart = 0;
            int bulletEnd = 0;
            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            }

            if (bulletStart < bulletEnd) {
                if (bulletColor >= 0 && bulletGapWidth >= 0) {
                    getEditableText().setSpan(new BulletSpan(bulletColor, bulletGapWidth), bulletStart, bulletEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (bulletGapWidth >= 0) {
                    getEditableText().setSpan(new BulletSpan(bulletGapWidth), bulletStart, bulletEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    getEditableText().setSpan(new BulletSpan(), bulletStart, bulletEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    protected void bulletInvalid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");

        for (int i = 0; i < lines.length; i++) {
            if (!containBullet(i)) {
                continue;
            }

            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1;
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            int bulletStart = 0;
            int bulletEnd = 0;
            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                bulletStart = lineStart;
                bulletEnd = lineEnd;
            }

            if (bulletStart < bulletEnd) {
                BulletSpan[] spans = getEditableText().getSpans(bulletStart, bulletEnd, BulletSpan.class);
                for (BulletSpan span : spans) {
                    getEditableText().removeSpan(span);
                }
            }
        }
    }

    protected boolean containBullet() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1;
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                list.add(i);
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                list.add(i);
            }
        }

        for (Integer i : list) {
            if (!containBullet(i)) {
                return false;
            }
        }

        return true;
    }

    protected boolean containBullet(int index) {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        if (index < 0 || index >= lines.length) {
            return false;
        }

        int start = 0;
        for (int i = 0; i < index; i++) {
            start = start + lines[i].length() + 1;
        }

        int end = start + lines[index].length();
        if (start >= end) {
            return false;
        }

        BulletSpan[] spans = getEditableText().getSpans(start, end, BulletSpan.class);
        return spans.length > 0;
    }

    // QuoteSpan ===================================================================================

    public void quote(boolean valid) {
        if (valid) {
            quoteValid();
        } else {
            quoteInvalid();
        }
    }

    protected void quoteValid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");

        for (int i = 0; i < lines.length; i++) {
            if (containQuote(i)) {
                continue;
            }

            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1; // \n
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            int quoteStart = 0;
            int quoteEnd = 0;
            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                quoteStart = lineStart;
                quoteEnd = lineEnd;
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                quoteStart = lineStart;
                quoteEnd = lineEnd;
            }

            if (quoteStart < quoteEnd) {
                if (quoteColor >= 0) {
                    getEditableText().setSpan(new QuoteSpan(quoteColor), quoteStart, quoteEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    getEditableText().setSpan(new QuoteSpan(), quoteStart, quoteEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    protected void quoteInvalid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");

        for (int i = 0; i < lines.length; i++) {
            if (!containQuote(i)) {
                continue;
            }

            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1;
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            int quoteStart = 0;
            int quoteEnd = 0;
            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                quoteStart = lineStart;
                quoteEnd = lineEnd;
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                quoteStart = lineStart;
                quoteEnd = lineEnd;
            }

            if (quoteStart < quoteEnd) {
                QuoteSpan[] spans = getEditableText().getSpans(quoteStart, quoteEnd, QuoteSpan.class);
                for (QuoteSpan span : spans) {
                    getEditableText().removeSpan(span);
                }
            }
        }
    }

    protected boolean containQuote() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            int lineStart = 0;
            for (int j = 0; j < i; j++) {
                lineStart = lineStart + lines[j].length() + 1;
            }

            int lineEnd = lineStart + lines[i].length();
            if (lineStart >= lineEnd) {
                continue;
            }

            if (lineStart <= getSelectionStart() && getSelectionEnd() <= lineEnd) {
                list.add(i);
            } else if (getSelectionStart() <= lineStart && lineEnd <= getSelectionEnd()) {
                list.add(i);
            }
        }

        for (Integer i : list) {
            if (!containQuote(i)) {
                return false;
            }
        }

        return true;
    }

    protected boolean containQuote(int index) {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        if (index < 0 || index >= lines.length) {
            return false;
        }

        int start = 0;
        for (int i = 0; i < index; i++) {
            start = start + lines[i].length() + 1;
        }

        int end = start + lines[index].length();
        if (start >= end) {
            return false;
        }

        QuoteSpan[] spans = getEditableText().getSpans(start, end, QuoteSpan.class);
        return spans.length > 0;
    }

    // URLSpan =====================================================================================

    public void link(String link) {
        link(link, getSelectionStart(), getSelectionEnd());
    }

    // When KnifeText lose focus, use this method
    public void link(String link, int start, int end) {
        if (link != null && !TextUtils.isEmpty(link.trim())) {
            linkValid(link, start, end);
        } else {
            linkInvalid(start, end);
        }
    }

    protected void linkValid(String link, int start, int end) {
        if (start >= end) {
            return;
        }

        linkInvalid(start, end);
        getEditableText().setSpan(new URLSpan(link), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    // Remove all span in selection, not like the boldInvalid()
    protected void linkInvalid(int start, int end) {
        if (start >= end) {
            return;
        }

        URLSpan[] spans = getEditableText().getSpans(start, end, URLSpan.class);
        for (URLSpan span : spans) {
            getEditableText().removeSpan(span);
        }
    }

    protected boolean containLink(int start, int end) {
        if (start > end) {
            return false;
        }

        if (start == end) {
            if (start - 1 < 0 || start + 1 > getEditableText().length()) {
                return false;
            } else {
                URLSpan[] before = getEditableText().getSpans(start - 1, start, URLSpan.class);
                URLSpan[] after = getEditableText().getSpans(start, start + 1, URLSpan.class);
                return before.length > 0 && after.length > 0;
            }
        } else {
            StringBuilder builder = new StringBuilder();

            for (int i = start; i < end; i++) {
                if (getEditableText().getSpans(i, i + 1, URLSpan.class).length > 0) {
                    builder.append(getEditableText().subSequence(i, i + 1).toString());
                }
            }

            return getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
    }

    // ImageSpan ===================================================================================

    // TODO

    // Helper ======================================================================================

    public void clearFormats() {
        setText(getEditableText().toString());
        setSelection(getEditableText().length());
    }

    public void hideSoftInput() {
        clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void showSoftInput() {
        requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
