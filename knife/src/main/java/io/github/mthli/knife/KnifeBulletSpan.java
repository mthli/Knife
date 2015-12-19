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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.BulletSpan;

public class KnifeBulletSpan extends BulletSpan {
    private static final int DEFAULT_COLOR = 0;
    private static final int DEFAULT_RADIUS = 3;
    private static final int DEFAULT_GAP_WIDTH = 2;
    private static Path bulletPath = null;

    private int bulletColor = DEFAULT_COLOR;
    private int bulletRadius = DEFAULT_RADIUS;
    private int bulletGapWidth = DEFAULT_GAP_WIDTH;

    public KnifeBulletSpan(int bulletColor, int bulletRadius, int bulletGapWidth) {
        this.bulletColor = bulletColor != 0 ? bulletColor : DEFAULT_COLOR;
        this.bulletRadius = bulletRadius != 0 ? bulletRadius : DEFAULT_RADIUS;
        this.bulletGapWidth = bulletGapWidth != 0 ? bulletGapWidth : DEFAULT_GAP_WIDTH;
    }

    public KnifeBulletSpan(Parcel src) {
        super(src);
        this.bulletColor = src.readInt();
        this.bulletRadius = src.readInt();
        this.bulletGapWidth = src.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(bulletColor);
        dest.writeInt(bulletRadius);
        dest.writeInt(bulletGapWidth);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 2 * bulletRadius + bulletGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = p.getStyle();

            int oldColor = p.getColor();
            p.setColor(bulletColor);
            p.setStyle(Paint.Style.FILL);

            if (c.isHardwareAccelerated()) {
                if (bulletPath == null) {
                    bulletPath = new Path();
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    bulletPath.addCircle(0.0f, 0.0f, bulletRadius, Path.Direction.CW);
                }

                c.save();
                c.translate(x + dir * bulletRadius, (top + bottom) / 2.0f);
                c.drawPath(bulletPath, p);
                c.restore();
            } else {
                c.drawCircle(x + dir * bulletRadius, (top + bottom) / 2.0f, bulletRadius, p);
            }

            p.setColor(oldColor);
            p.setStyle(style);
        }
    }
}