package io.github.mthli.knife;

public class KnifePart {
    private int start;
    private int end;

    public KnifePart(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isValid() {
        return start < end;
    }
}
