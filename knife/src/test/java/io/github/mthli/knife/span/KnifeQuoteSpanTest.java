package io.github.mthli.knife.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.Layout;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import io.github.mthli.knife.BaseKnifeTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class KnifeQuoteSpanTest extends BaseKnifeTest {

    @Test public void constructorWithValues() {
        KnifeQuoteSpan knifeQuoteSpan = new KnifeQuoteSpan(1, 2, 3);
        Parcel parcel = mock(Parcel.class);
        knifeQuoteSpan.writeToParcel(parcel, 0);
        verify(parcel).writeInt(1);
        verify(parcel).writeInt(2);
        verify(parcel).writeInt(3);
    }

    @Test public void constructorWithDefaults() {
        KnifeQuoteSpan knifeQuoteSpan = new KnifeQuoteSpan(0, 0, 0);
        Parcel parcel = mock(Parcel.class);
        knifeQuoteSpan.writeToParcel(parcel, 0);
        verify(parcel, times(2)).writeInt(0xff0000ff); // colour is also this value.
        verify(parcel, times(2)).writeInt(2);
    }

    @Test public void getLeadingMargin() {
        KnifeQuoteSpan knifeQuoteSpan = new KnifeQuoteSpan(0, 10, 20);
        assertEquals(30, knifeQuoteSpan.getLeadingMargin(true));
        assertEquals(30, knifeQuoteSpan.getLeadingMargin(false));
    }

    @Test public void drawLeadingMargin() {

        Canvas canvas = mock(Canvas.class);
        Paint paint = mock(Paint.class);
        Paint.Style style = Paint.Style.FILL_AND_STROKE;
        int color = Color.YELLOW;
        when(paint.getColor()).thenReturn(color);
        when(paint.getStyle()).thenReturn(style);

        int x = 10;
        int direction = 2;
        int top = 10;
        int baseline = 20;
        int bottom = 40;

        KnifeQuoteSpan knifeQuoteSpan = new KnifeQuoteSpan(Color.RED, 10, 20);
        knifeQuoteSpan.drawLeadingMargin(canvas, paint, x, direction, top, baseline,
            bottom, "", 1, 2, false, mock(Layout.class));

        verify(paint).setStyle(Paint.Style.FILL);
        verify(paint).setColor(Color.RED);
        verify(canvas).drawRect(x, top, x + direction * 20, bottom, paint);
        verify(paint).setStyle(style);
        verify(paint).setColor(color);
    }

    @Test public void parcelConstructor() {
        Parcel parcel = mock(Parcel.class);
        when(parcel.readInt()).thenAnswer(getParcelAnswer());
        KnifeQuoteSpan span = new KnifeQuoteSpan(parcel);
        assertEquals(new KnifeQuoteSpan(1, 2, 3), span);
    }

    @Test public void equality() {
        assertEquals(new KnifeQuoteSpan(1, 2, 3).hashCode(),
            new KnifeQuoteSpan(1, 2, 3).hashCode());
    }

    private Answer<Integer> getParcelAnswer() {
        return new Answer<Integer>() {
            int times = -1;

            @Override public Integer answer(InvocationOnMock invocation) throws Throwable {
                times++;
                return times;
            }
        };
    }
}
