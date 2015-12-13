package io.github.mthli.knife;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

public class KnifeURLSpan extends URLSpan {
    private int linkColor = -1;
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
        if (linkColor < 0) {
            ds.setColor(ds.linkColor);
        } else {
            ds.setColor(linkColor);
        }

        ds.setUnderlineText(linkUnderline);
    }
}
