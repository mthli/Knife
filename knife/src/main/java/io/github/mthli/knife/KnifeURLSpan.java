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

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

public class KnifeURLSpan extends URLSpan {
    private int linkColor = 0;
    private boolean linkUnderline = true;

    public KnifeURLSpan(String url, int linkColor, boolean linkUnderline) {
        super(url);
        this.linkColor = linkColor;
        this.linkUnderline = linkUnderline;
    }

    public KnifeURLSpan(Parcel src, int linkColor, boolean linkUnderline) {
        super(src);
        this.linkColor = linkColor;
        this.linkUnderline = linkUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (linkColor == 0) {
            ds.setColor(ds.linkColor);
        } else {
            ds.setColor(linkColor);
        }

        ds.setUnderlineText(linkUnderline);
    }
}
