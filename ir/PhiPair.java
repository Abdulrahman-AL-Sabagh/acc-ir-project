package ir;

public class PhiPair extends Node {
    Node lhs;
    Node rhs;

    public PhiPair(Instruction lhs, Instruction rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Node getLhs() {
        return lhs;
    }

    public void setLhs(Node lhs) {
        this.lhs = lhs;
    }

    public Node getRhs() {
        return rhs;
    }

    public void setRhs(Node rhs) {
        this.rhs = rhs;
    }

}
