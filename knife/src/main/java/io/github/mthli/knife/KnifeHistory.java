package io.github.mthli.knife;

public class KnifeHistory {
    private CharSequence text;
    private int start;
    private int count;
    private int after;

    public KnifeHistory(CharSequence text, int start, int count, int after) {
        this.text = text;
        this.start = start;
        this.count = count;
        this.after = after;
    }

    public CharSequence getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getCount() {
        return count;
    }

    public int getAfter() {
        return after;
    }
}
