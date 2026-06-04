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
}
