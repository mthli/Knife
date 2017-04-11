package io.github.mthli.knife.span;

import android.graphics.Color;
import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.TextPaint;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import io.github.mthli.knife.BaseKnifeTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class KnifeUrlSpanTest extends BaseKnifeTest {

    @Test public void constructor() {
        String url = "test url";
        KnifeURLSpan knifeURLSpan = new KnifeURLSpan(url, Color.BLUE, false);
        Parcel parcel = mock(Parcel.class);
        knifeURLSpan.writeToParcel(parcel, 0);
        verify(parcel).writeString(url);
        verify(parcel).writeInt(Color.BLUE);
        verify(parcel).writeInt(0);
    }

    @Test public void parcelConstructor() {
        Parcel parcel = mock(Parcel.class);
        when(parcel.readInt()).thenAnswer(getParcelAnswer());
        when(parcel.readString()).thenReturn("ParcelString");

        KnifeURLSpan knifeURLSpanParcel = new KnifeURLSpan(parcel);
        KnifeURLSpan knifeURLSpan = new KnifeURLSpan("ParcelString", Color.CYAN, true);
        assertEquals(knifeURLSpan, knifeURLSpanParcel);
    }

    @Test public void hashCodeEquality() {
        KnifeURLSpan knifeURLSpan1 = new KnifeURLSpan("ParcelString", Color.CYAN, true);
        KnifeURLSpan knifeURLSpan2 = new KnifeURLSpan("ParcelString", Color.CYAN, true);
        assertEquals(knifeURLSpan1, knifeURLSpan2);
        assertEquals(knifeURLSpan1.hashCode(), knifeURLSpan2.hashCode());
    }

    @Test public void updateDrawState() {
        KnifeURLSpan knifeURLSpan = new KnifeURLSpan("http://example.com", Color.CYAN, true);
        TextPaint textPaint = mock(TextPaint.class);
        knifeURLSpan.updateDrawState(textPaint);
        verify(textPaint).setColor(Color.CYAN);
        verify(textPaint).setUnderlineText(true);
    }

    @Test public void updateDrawStateInvalidValues() {
        KnifeURLSpan knifeURLSpan = new KnifeURLSpan("http://example.com", 0, false);
        TextPaint textPaint = mock(TextPaint.class);
        textPaint.linkColor = Color.DKGRAY;
        knifeURLSpan.updateDrawState(textPaint);
        verify(textPaint).setColor(Color.DKGRAY);
        verify(textPaint).setUnderlineText(false);
    }

    private Answer<Object> getParcelAnswer() {
        return new Answer<Object>() {
            int times = -1;

            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                times++;
                if (times == 0) {
                    return Color.CYAN;
                } else {
                    return 1;
                }
            }
        };
    }
}
