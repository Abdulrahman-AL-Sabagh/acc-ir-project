package ir;

public class Instruction extends Node {
    private final Node x;
    private final Code.OpCode opCode;
    private final Node y;

    public Instruction(Node x, Code.OpCode opCode, Node y) {
        this.x = x;
        this.opCode = opCode;
        this.y = y;

    }

    public Node getX() {
        return x;
    }

    public Code.OpCode getOpCode() {
        return opCode;
    }

    public Node getY() {
        return y;
    }

    private String stringifyNode(Node n) {
        if (n instanceof Instruction) {
            return String.format("(%d)", n.getId());
        } else return n.toString();
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", stringifyNode(x), opCode.name(), stringifyNode(y));
    }
}
