package ir;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Constant constant = (Constant) o;
        return val == constant.val;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }
}
