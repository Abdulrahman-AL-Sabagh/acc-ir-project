package ir;

public class Label {

    private Code.OpCode op;
    private Instruction fix;
    private Node cond;

    public Label(Code.OpCode op, Instruction fix, Node cond) {
        this.op = op;
        this.fix = fix;
        this.cond = cond;
    }

    public Code.OpCode getOp() {
        return op;
    }

    public void setOp(Code.OpCode op) {
        this.op = op;
    }

    public Instruction getFix() {
        return fix;
    }

    public void setFix(Instruction fix) {
        this.fix = fix;
    }

    public Node getCond() {
        return cond;
    }

    public void setCond(Node cond) {
        this.cond = cond;
    }
}
