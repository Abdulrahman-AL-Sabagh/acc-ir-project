package ir;

public class Instruction extends Node {
    private  Node x;
    private  Code.OpCode opCode;
    private Node y;
    private final Block block;

    public Instruction(Node x, Code.OpCode opCode, Node y, Block block) {
        this.x = x;
        this.opCode = opCode;
        this.y = y;
        this.block = block;

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
        if (n == null) {
            return "_";
        }
        if (n instanceof Instruction) {
            return String.format("(%d)", n.getId());
        } else return n.toString();
    }

    public void setY(Node y) {
        this.y = y;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", stringifyNode(x), opCode.name(), stringifyNode(y));
    }

    public void setX(Node x) {
        this.x = x;
    }

    public void setOpCode(Code.OpCode opCode) {
        this.opCode = opCode;
    }
}
