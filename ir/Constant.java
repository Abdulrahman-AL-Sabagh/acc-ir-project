package ir;

public class Constant extends Node {
    private final int val;

    public Constant(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return String.format("%d", val);
    }
}
