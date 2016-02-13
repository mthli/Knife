package io.github.mthli.knife;

import android.test.suitebuilder.annotation.SmallTest;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class KnifeTagHandlerTest extends BaseKnifeTest {

    @Test public void start() {
        Editable editable = mock(Editable.class);
        when(editable.length()).thenReturn(10);
        Object object = mock(Object.class);
        KnifeTagHandler knifeTagHandler = new KnifeTagHandler();
        knifeTagHandler.start(editable, object);
        verify(editable).setSpan(object, 10, 10, Spanned.SPAN_MARK_MARK);
    }

    @Test public void getLast() {
        Editable text = mock(Editable.class);
        Class klass = Object.class;
        when(text.length()).thenReturn(10);
        Object[] spans = new Object[]{};
        when(text.getSpans(0, 10, klass)).thenReturn(spans);
        assertNull(KnifeTagHandler.getLast(text, klass));
    }

    @Test public void getLastNotFound() {
        Editable text = mock(Editable.class);
        Class klass = Object.class;
        when(text.length()).thenReturn(10);
        Object[] spans = new Object[]{1, 2, 3};
        when(text.getSpans(0, 10, klass)).thenReturn(spans);
        assertNull(KnifeTagHandler.getLast(text, klass));
    }

    @Test public void getLastSpan() {
        Editable text = mock(Editable.class);
        Class klass = Object.class;
        when(text.length()).thenReturn(10);
        int spannable = Spannable.SPAN_MARK_MARK;
        when(text.getSpanFlags(spannable)).thenReturn(spannable);
        Object[] spans = new Object[]{1, spannable, 3};
        when(text.getSpans(0, 10, klass)).thenReturn(spans);
        assertEquals(spannable, KnifeTagHandler.getLast(text, klass));
    }

    @Test public void end() {
        Editable text = mock(Editable.class);
        Class klass = Object.class;
        when(text.length()).thenReturn(10);
        int spannable = Spannable.SPAN_MARK_MARK;
        when(text.getSpanFlags(spannable)).thenReturn(spannable);
        Object[] spans = new Object[]{1, spannable, 3};
        when(text.getSpans(0, 10, klass)).thenReturn(spans);
        KnifeTagHandler knifeTagHandler = new KnifeTagHandler();
        knifeTagHandler.end(text, klass, 0);
    }
}
