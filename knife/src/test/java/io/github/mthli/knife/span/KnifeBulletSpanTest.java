package io.github.mthli.knife.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;
import android.text.Layout;
import android.text.Spanned;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import io.github.mthli.knife.BaseKnifeTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SmallTest
public class KnifeBulletSpanTest extends BaseKnifeTest {

    @Test public void constructor() {
        KnifeBulletSpan knifeBulletSpan = new KnifeBulletSpan(Color.GREEN, 10, 20);
        Parcel parcel = mock(Parcel.class);
        knifeBulletSpan.writeToParcel(parcel, 0);
        verify(parcel).writeInt(Color.GREEN);
        verify(parcel).writeInt(10);
        verify(parcel).writeInt(20);
    }

    @Test public void getLeadingMargin() {
        KnifeBulletSpan knifeBulletSpan = new KnifeBulletSpan(Color.GREEN, 10, 20);
        assertEquals(40, knifeBulletSpan.getLeadingMargin(true));
        assertEquals(40, knifeBulletSpan.getLeadingMargin(false));
    }

    @Test public void parcel() {
        Parcel parcel = mock(Parcel.class);
        when(parcel.readInt()).thenAnswer(getParcelAnswer());
        KnifeBulletSpan knifeBulletSpan = new KnifeBulletSpan(parcel);
        assertEquals(new KnifeBulletSpan(Color.CYAN, 10, 20), knifeBulletSpan);
    }

    @Test public void equality() {
        KnifeBulletSpan knifeBulletSpan = new KnifeBulletSpan(Color.CYAN, 10, 20);
        KnifeBulletSpan knifeBulletSpan1 = new KnifeBulletSpan(Color.CYAN, 10, 20);
        assertEquals(knifeBulletSpan, knifeBulletSpan1);
        assertEquals(knifeBulletSpan.hashCode(), knifeBulletSpan1.hashCode());
    }

    @Test public void drawLeadingMargin() {
        Canvas canvas = mock(Canvas.class);
        when(canvas.isHardwareAccelerated()).thenReturn(true);
        Paint paint = mock(Paint.class);
        when(paint.getColor()).thenReturn(Color.RED);
        Paint.Style style = Paint.Style.FILL_AND_STROKE;
        when(paint.getStyle()).thenReturn(style);
        int x = 10;
        int dir = 10;
        int top = 10;
        int baseline = 10;
        int bottom = 20;
        Spanned charSequence = mock(Spanned.class);
        int start = 10;
        int end = 20;
        boolean first = true;
        Layout layout = mock(Layout.class);

        KnifeBulletSpan knifeBulletSpan = new KnifeBulletSpan(Color.GREEN, 10, 20);
        when((charSequence).getSpanStart(knifeBulletSpan)).thenReturn(start);
        knifeBulletSpan.drawLeadingMargin(canvas, paint, x, dir, top, baseline, bottom, charSequence, start, end, first,
            layout);

        verify(paint).setColor(Color.GREEN);
        verify(paint).setStyle(Paint.Style.FILL);
        verify(canvas).save();
        verify(canvas).translate(x + dir * 10, (top + bottom) / 2.0f);
        verify(canvas).drawPath(any(Path.class), Matchers.eq(paint));
        verify(canvas).restore();

        verify(paint).setColor(Color.RED);
        verify(paint).setStyle(style);
    }

    @Test public void drawLeadingMarginNoHardwareAcceleration() {
        Canvas canvas = mock(Canvas.class);
        when(canvas.isHardwareAccelerated()).thenReturn(false);
        KnifeBulletSpan knifeBulletSpan = new KnifeBulletSpan(Color.GREEN, 10, 20);
        Paint paint = mock(Paint.class);
        Spanned charSequence = mock(Spanned.class);
        int x = 30;
        int dir = 400;
        int top = 100;
        int bottom = 100;
        int start = 20;
        when((charSequence).getSpanStart(knifeBulletSpan)).thenReturn(start);
        knifeBulletSpan.drawLeadingMargin(canvas, paint, x, dir, top, 0, bottom, charSequence, start, 0, true, mock
            (Layout.class));
        verify(canvas).drawCircle(x + dir * 10, (top + bottom) / 2.0f, 10, paint);
    }

    private Answer<Object> getParcelAnswer() {
        return new Answer<Object>() {
            int times = -1;

            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                times++;
                if (times == 3) {
                    return Color.CYAN;
                } else if (times == 4) {
                    return 10;
                } else {
                    return 20;
                }
            }
        };
    }

}
