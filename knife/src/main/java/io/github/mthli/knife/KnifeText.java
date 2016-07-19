/*
 * Copyright (C) 2015 Matthew Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mthli.knife;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import io.github.mthli.knife.history.KnifeHistory;
import io.github.mthli.knife.history.action.Action;
import io.github.mthli.knife.history.action.SequentialAction;
import io.github.mthli.knife.history.action.SpanAddedAction;
import io.github.mthli.knife.history.action.SpanRemovedAction;
import io.github.mthli.knife.history.action.TextChangedAction;
import io.github.mthli.knife.history.TextChangedRecord;
import io.github.mthli.knife.history.action.TextReplacedAction;

public class KnifeText extends EditText implements TextWatcher {

    public static final Class[] KNIFE_SPAN_CLASSES = new Class[]{StrikethroughSpan.class
            , StyleSpan.class, URLSpan.class, KnifeURLSpan.class, QuoteSpan.class, UnderlineSpan.class, BulletSpan.class};


    public static final int FORMAT_BOLD = 0x01;
    public static final int FORMAT_ITALIC = 0x02;
    public static final int FORMAT_UNDERLINED = 0x03;
    public static final int FORMAT_STRIKETHROUGH = 0x04;
    public static final int FORMAT_BULLET = 0x05;
    public static final int FORMAT_QUOTE = 0x06;
    public static final int FORMAT_LINK = 0x07;

    private int bulletColor = 0;
    private int bulletRadius = 0;
    private int bulletGapWidth = 0;
    private int historySize = 100;
    private int linkColor = 0;
    private boolean linkUnderline = true;
    private int quoteColor = 0;
    private int quoteStripeWidth = 0;
    private int quoteGapWidth = 0;

    private KnifeHistory history = new KnifeHistory();

    private SpannableStringBuilder inputBefore;

    public KnifeHistory.HistoryStateChangeListener getHistoryStateChangeListener() {
        return history.getStateChangeListener();
    }

    public void setHistoryStateChangeListener(KnifeHistory.HistoryStateChangeListener historyStateChangeListener) {
        history.setStateChangeListener(historyStateChangeListener);
    }

    private Editable inputLast;

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
        bulletColor = array.getColor(R.styleable.KnifeText_bulletColor, 0);
        bulletRadius = array.getDimensionPixelSize(R.styleable.KnifeText_bulletRadius, 0);
        bulletGapWidth = array.getDimensionPixelSize(R.styleable.KnifeText_bulletGapWidth, 0);
        history.setEnabled(array.getBoolean(R.styleable.KnifeText_historyEnable, true));
        history.setMaxCapacity(array.getInt(R.styleable.KnifeText_maxHistoryCapacity, KnifeHistory.DEFAULT_MAX_CAPACITY));
        linkColor = array.getColor(R.styleable.KnifeText_linkColor, 0);
        linkUnderline = array.getBoolean(R.styleable.KnifeText_linkUnderline, true);
        quoteColor = array.getColor(R.styleable.KnifeText_quoteColor, 0);
        quoteStripeWidth = array.getDimensionPixelSize(R.styleable.KnifeText_quoteStripeWidth, 0);
        quoteGapWidth = array.getDimensionPixelSize(R.styleable.KnifeText_quoteCapWidth, 0);
        array.recycle();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addTextChangedListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(this);
    }

    // StyleSpan ===================================================================================

    public void bold(boolean valid) {
        Action action;
        if (valid) {
            action = styleValid(Typeface.BOLD, getSelectionStart(), getSelectionEnd());
        } else {
            action = styleInvalid(Typeface.BOLD, getSelectionStart(), getSelectionEnd());
        }
        history.record(action);
    }

    public void italic(boolean valid) {
        Action action;
        if (valid) {
            action = styleValid(Typeface.ITALIC, getSelectionStart(), getSelectionEnd());
        } else {
            action = styleInvalid(Typeface.ITALIC, getSelectionStart(), getSelectionEnd());
        }
        history.record(action);
    }

    protected Action styleValid(int style, int start, int end) {
        switch (style) {
            case Typeface.NORMAL:
            case Typeface.BOLD:
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                break;
            default:
                return null;
        }

        if (start >= end) {
            return null;
        }

        StyleSpan newSpan = new StyleSpan(style);
        getEditableText().setSpan(newSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpanAddedAction(newSpan, start, end);
    }

    protected Action styleInvalid(int style, int start, int end) {
        switch (style) {
            case Typeface.NORMAL:
            case Typeface.BOLD:
            case Typeface.ITALIC:
            case Typeface.BOLD_ITALIC:
                break;
            default:
                return null;
        }

        if (start >= end) {
            return null;
        }

        Editable editableText = getEditableText();
        StyleSpan[] spans = editableText.getSpans(start, end, StyleSpan.class);
        List<KnifePart> list = new ArrayList<>();

        List<Action> actions = new ArrayList<>(spans.length);
        for (StyleSpan span : spans) {
            int spanStart = editableText.getSpanStart(span);
            int spanEnd = editableText.getSpanEnd(span);
            list.add(new KnifePart(spanStart, spanEnd));
            editableText.removeSpan(span);
            actions.add(new SpanRemovedAction(span, spanStart, spanEnd));
        }

        for (KnifePart part : list) {
            if (part.isValid()) {
                if (part.getStart() < start) {
                    Action action = styleValid(style, part.getStart(), start);
                    if (action != null) {
                        actions.add(action);
                    }
                }

                if (part.getEnd() > end) {
                    Action action = styleValid(style, end, part.getEnd());
                    if (action != null) {
                        actions.add(action);
                    }
                }
            }
        }

        return new SequentialAction(actions.toArray(new Action[0]));
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
        Action action;
        if (valid) {
            action = underlineValid(getSelectionStart(), getSelectionEnd());
        } else {
            action = underlineInvalid(getSelectionStart(), getSelectionEnd());
        }
        history.record(action);
    }

    protected Action underlineValid(int start, int end) {
        if (start >= end) {
            return null;
        }

        UnderlineSpan newSpan = new UnderlineSpan();
        getEditableText().setSpan(newSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpanAddedAction(newSpan, start, end);
    }

    protected Action underlineInvalid(int start, int end) {
        if (start >= end) {
            return null;
        }

        Editable editableText = getEditableText();
        UnderlineSpan[] spans = editableText.getSpans(start, end, UnderlineSpan.class);
        List<KnifePart> list = new ArrayList<>();

        List<Action> actions = new ArrayList<>(spans.length);
        for (UnderlineSpan span : spans) {
            int spanStart = editableText.getSpanStart(span);
            int spanEnd = editableText.getSpanEnd(span);
            list.add(new KnifePart(spanStart, spanEnd));
            editableText.removeSpan(span);
            actions.add(new SpanRemovedAction(span, spanStart, spanEnd));
        }

        for (KnifePart part : list) {
            if (part.isValid()) {
                if (part.getStart() < start) {
                    Action action = underlineValid(part.getStart(), start);
                    if (action != null) {
                        actions.add(action);
                    }
                }

                if (part.getEnd() > end) {
                    Action action = underlineValid(end, part.getEnd());
                    if (action != null) {
                        actions.add(action);
                    }
                }
            }
        }
        return new SequentialAction(actions.toArray(new Action[0]));
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
        Action action;
        if (valid) {
            action = strikethroughValid(getSelectionStart(), getSelectionEnd());
        } else {
            action = strikethroughInvalid(getSelectionStart(), getSelectionEnd());
        }
        history.record(action);
    }

    protected Action strikethroughValid(int start, int end) {
        if (start >= end) {
            return null;
        }

        StrikethroughSpan newSpan = new StrikethroughSpan();
        getEditableText().setSpan(newSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpanAddedAction(newSpan, start, end);
    }

    protected Action strikethroughInvalid(int start, int end) {
        if (start >= end) {
            return null;
        }

        Editable editableText = getEditableText();
        StrikethroughSpan[] spans = editableText.getSpans(start, end, StrikethroughSpan.class);
        List<KnifePart> list = new ArrayList<>();

        List<Action> actions = new ArrayList<>(spans.length);
        for (StrikethroughSpan span : spans) {
            int spanStart = editableText.getSpanStart(span);
            int spanEnd = editableText.getSpanEnd(span);
            list.add(new KnifePart(spanStart, spanEnd));
            editableText.removeSpan(span);
            actions.add(new SpanRemovedAction(span, spanStart, spanEnd));
        }

        for (KnifePart part : list) {
            if (part.isValid()) {
                if (part.getStart() < start) {
                    Action action = strikethroughValid(part.getStart(), start);
                    if (action != null) {
                        actions.add(action);
                    }
                }

                if (part.getEnd() > end) {
                    Action action = strikethroughValid(end, part.getEnd());
                    if (action != null) {
                        actions.add(action);
                    }
                }
            }
        }

        return new SequentialAction(actions.toArray(new Action[0]));
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
                KnifeBulletSpan newSpan = new KnifeBulletSpan(bulletColor, bulletRadius, bulletGapWidth);
                getEditableText().setSpan(newSpan, bulletStart, bulletEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                history.record(new SpanAddedAction(newSpan, bulletStart, bulletEnd));
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
                Editable editableText = getEditableText();
                BulletSpan[] spans = editableText.getSpans(bulletStart, bulletEnd, BulletSpan.class);
                for (BulletSpan span : spans) {
                    editableText.removeSpan(span);
                    history.record(new SpanRemovedAction(span, editableText.getSpanStart(span), editableText.getSpanEnd(span)));
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
        Action action;
        if (valid) {
            action = quoteValid();
        } else {
            action = quoteInvalid();
        }
        history.record(action);
    }

    protected Action quoteValid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        List<Action> actions = new ArrayList<>();
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
                KnifeQuoteSpan newSpan = new KnifeQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth);
                getEditableText().setSpan(newSpan, quoteStart, quoteEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                actions.add(new SpanAddedAction(newSpan, quoteStart, quoteEnd));
            }
        }
        return new SequentialAction(actions.toArray(new Action[0]));
    }

    protected Action quoteInvalid() {
        String[] lines = TextUtils.split(getEditableText().toString(), "\n");
        List<Action> actions = new ArrayList<>();
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
                Editable editableText = getEditableText();
                QuoteSpan[] spans = editableText.getSpans(quoteStart, quoteEnd, QuoteSpan.class);
                List<Action> subActions = new ArrayList<>(spans.length);
                for (QuoteSpan span : spans) {
                    editableText.removeSpan(span);
                    subActions.add(new SpanRemovedAction(span, editableText.getSpanStart(span), editableText.getSpanEnd(span)));
                }
               actions.add(new SequentialAction(actions.toArray(new Action[0])));
            }
        }
        return new SequentialAction(actions.toArray(new Action[0]));
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
        KnifeURLSpan newSpan = new KnifeURLSpan(link, linkColor, linkUnderline);
        getEditableText().setSpan(newSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        history.record(new SpanAddedAction(newSpan, start, end));
    }

    // Remove all span in selection, not like the boldInvalid()
    protected void linkInvalid(int start, int end) {
        if (start >= end) {
            return;
        }

        Editable editableText = getEditableText();
        URLSpan[] spans = editableText.getSpans(start, end, URLSpan.class);
        List<Action> actions = new ArrayList<>(spans.length);
        for (URLSpan span : spans) {
            editableText.removeSpan(span);
            actions.add(new SpanRemovedAction(span, editableText.getSpanStart(span), editableText.getSpanEnd(span)));
        }
        history.record(new SequentialAction(actions.toArray(new Action[0])));
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

    // Redo/Undo ===================================================================================

    TextChangedRecord beforeRecord, afterRecord;

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        if (!history.isEnabled() || history.isProcessing()) {
            return;
        }

        beforeRecord = new TextChangedRecord(getEditableText(), text, start, count, KNIFE_SPAN_CLASSES);
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        afterRecord = new TextChangedRecord(getEditableText(), text, start, count, KNIFE_SPAN_CLASSES);
    }

    @Override
    public void afterTextChanged(Editable text) {
        if (!history.isEnabled() || history.isProcessing()) {
            return;
        }

        history.record(new TextChangedAction(beforeRecord, afterRecord));
    }

    public void redo() {
        if (!history.isRedoable()) {
            return;
        }

        history.redo(getEditableText());
    }

    public void undo() {
        if (!history.isUndoable()) {
            return;
        }

        history.undo(getEditableText());
    }

    public boolean isRedoable() {
        return history.isRedoable();
    }

    public boolean isUndoable() {
        return history.isUndoable();
    }

    public void clearHistory() {
        history.clear();
    }

    // Helper ======================================================================================

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
            default:
                return false;
        }
    }

    public void clearFormats() {
        setKnifeText(getEditableText().toString());
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

    public void fromHtml(String source) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(KnifeParser.fromHtml(source));
        switchToKnifeStyle(builder, 0, builder.length());
//        setKnifeText(builder);
        setText(builder);
    }

    public String toHtml() {
        return KnifeParser.toHtml(getEditableText());
    }

    protected void switchToKnifeStyle(Editable editable, int start, int end) {
        BulletSpan[] bulletSpans = editable.getSpans(start, end, BulletSpan.class);
        for (BulletSpan span : bulletSpans) {
            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);
            spanEnd = 0 < spanEnd && spanEnd < editable.length() && editable.charAt(spanEnd) == '\n' ? spanEnd - 1 : spanEnd;
            editable.removeSpan(span);
            KnifeBulletSpan newSpan = new KnifeBulletSpan(bulletColor, bulletRadius, bulletGapWidth);
            editable.setSpan(newSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//            history.record(new SpanReplacedAction(spanStart,spanEnd,span,newSpan));
        }

        QuoteSpan[] quoteSpans = editable.getSpans(start, end, QuoteSpan.class);
        for (QuoteSpan span : quoteSpans) {
            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);
            spanEnd = 0 < spanEnd && spanEnd < editable.length() && editable.charAt(spanEnd) == '\n' ? spanEnd - 1 : spanEnd;
            editable.removeSpan(span);
            KnifeQuoteSpan newSpan = new KnifeQuoteSpan(quoteColor, quoteStripeWidth, quoteGapWidth);
            editable.setSpan(newSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//            history.record(new SpanReplacedAction(spanStart,spanEnd,span,newSpan));
        }

        URLSpan[] urlSpans = editable.getSpans(start, end, URLSpan.class);
        for (URLSpan span : urlSpans) {
            int spanStart = editable.getSpanStart(span);
            int spanEnd = editable.getSpanEnd(span);
            editable.removeSpan(span);
            KnifeURLSpan newSpan = new KnifeURLSpan(span.getURL(), linkColor, linkUnderline);
            editable.setSpan(newSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//            history.record(new SpanReplacedAction(spanStart,spanEnd,span,newSpan));
        }
    }

    /**
     * this method will invoke history recording
     *
     * @param charSequence
     */
    public void setKnifeText(CharSequence charSequence) {
        history.record(new TextReplacedAction(0, getText(), charSequence));
        setText(charSequence);
    }
}
