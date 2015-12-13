package io.github.mthli.knife;

public class KnifeHistory {
    private CharSequence text;
    private int start;
    private int count;
    private int change;

    public KnifeHistory(CharSequence text, int start, int count, int change) {
        this.text = text;
        this.start = start;
        this.count = count;
        this.change = change;
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

    public int getChange() {
        return change;
    }
}
