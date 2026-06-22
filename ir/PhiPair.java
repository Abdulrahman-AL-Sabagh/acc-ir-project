package ir;

public class PhiPair extends Node {
    Instruction lhs;
    Instruction rhs;

    public PhiPair(Instruction lhs, Instruction rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Instruction getLhs() {
        return lhs;
    }

    public void setLhs(Instruction lhs) {
        this.lhs = lhs;
    }

    public Instruction getRhs() {
        return rhs;
    }

    public void setRhs(Instruction rhs) {
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", lhs != null ? lhs.getId() : "?" , rhs != null ? rhs.getId() : "?");
    }
}
