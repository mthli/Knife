package io.github.mthli.knife;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    private boolean containBold() {
        int start = getSelectionStart();
        int end = getSelectionEnd();

        if (start == end) {
            if (start - 1 < 0 || start + 1 > getEditableText().length()) {
                return false;
            } else {
                StyleSpan[] before = getEditableText().getSpans(start - 1, start, StyleSpan.class);
                StyleSpan[] after = getEditableText().getSpans(start, start + 1, StyleSpan.class);
                return before.length > 0 && after.length > 0 && before[0].getStyle() == Typeface.BOLD && after[0].getStyle() == Typeface.BOLD;
            }
        } else {
            StringBuilder builder = new StringBuilder();

            // Make sure no duplicate characters be added
            for (int i = start; i < end; i++) {
                StyleSpan[] spans = getEditableText().getSpans(i, i + 1, StyleSpan.class);
                for (StyleSpan span : spans) {
                    if (span.getStyle() == Typeface.BOLD) {
                        builder.append(getEditableText().subSequence(i, i + 1).toString());
                        break;
                    }
                }
            }

            return getEditableText().subSequence(start, end).toString().equals(builder.toString());
        }
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

    // Bold ========================================================================================

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
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();

        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(getEditableText().subSequence(selectionStart, selectionEnd));
        StyleSpan[] spans = spanBuilder.getSpans(0, spanBuilder.length(), StyleSpan.class);
        for (StyleSpan span : spans) {
            if (span.getStyle() == Typeface.BOLD) {
                spanBuilder.removeSpan(span);
            }
        }

        SpannableStringBuilder contentBuilder = new SpannableStringBuilder();
        contentBuilder.append(getEditableText().subSequence(0, selectionStart));
        contentBuilder.append(spanBuilder);
        contentBuilder.append(getEditableText().subSequence(selectionEnd, getEditableText().length()));
        setText(contentBuilder);
        setSelection(selectionStart, selectionEnd);
        showSelections();
    }

    // Selection ===================================================================================
    // http://stackoverflow.com/questions/22987855/how-to-show-selection-marker-on-edittext

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        if (selStart >= selEnd) {
            hideSelections();
        }

        super.onSelectionChanged(selStart, selEnd);
    }

    private void showSelections() {
        try {
            Field mEditor = TextView.class.getDeclaredField("mEditor");
            mEditor.setAccessible(true);
            Object editor = mEditor.get(this);

            Method getSelectionController = editor.getClass().getDeclaredMethod("getSelectionController");
            getSelectionController.setAccessible(true);
            Object controller = getSelectionController.invoke(editor);

            if (controller != null) {
                Method show = controller.getClass().getDeclaredMethod("show");
                show.setAccessible(true);
                show.invoke(controller);
            }
        } catch (NoSuchFieldException n) {
            n.printStackTrace();
        } catch (NoSuchMethodException n) {
            n.printStackTrace();
        } catch (IllegalAccessException i) {
            i.printStackTrace();
        } catch (InvocationTargetException i) {
            i.printStackTrace();
        }
    }

    private void hideSelections() {
        try {
            Field mEditor = TextView.class.getDeclaredField("mEditor");
            mEditor.setAccessible(true);
            Object editor = mEditor.get(this);

            Method getSelectionController = editor.getClass().getDeclaredMethod("getSelectionController");
            getSelectionController.setAccessible(true);
            Object controller = getSelectionController.invoke(editor);

            if (controller != null) {
                Method hide = controller.getClass().getDeclaredMethod("hide");
                hide.setAccessible(true);
                hide.invoke(controller);
            }
        } catch (NoSuchFieldException n) {
            n.printStackTrace();
        } catch (NoSuchMethodException n) {
            n.printStackTrace();
        } catch (IllegalAccessException i) {
            i.printStackTrace();
        } catch (InvocationTargetException i) {
            i.printStackTrace();
        }
    }
}
