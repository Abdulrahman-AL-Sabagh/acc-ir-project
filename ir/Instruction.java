package ir;

import java.util.Objects;

public class Instruction extends Node {
    private int id;
    private Node x;
    private Code.OpCode opCode;
    private Node y;
    private final Block block;
    private static int idGenerator = 0;
    private Instruction opLink;
    private Instruction prev;
    private Instruction next;
    boolean eliminated = false;
    public Instruction(Node x, Code.OpCode opCode, Node y, Block block) {
        this.x = x;
        this.opCode = opCode;
        this.y = y;
        this.block = block;

        this.id = idGenerator++;
        super.setType(SymbolTable.intType);


    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return Objects.equals(x, that.x) && opCode == that.opCode && Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, opCode, y);
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
        if (n instanceof Instruction var) {
            return String.format("(%d)", var.getId());
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
        return String.format("%s: %s %s %s", this.getId(), stringifyNode(x), opCode.name(), stringifyNode(y));
    }

    public void setX(Node x) {
        this.x = x;
    }

    public void setOpCode(Code.OpCode opCode) {
        this.opCode = opCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Instruction getOpLink() {
        return opLink;
    }

    public void setOpLink(Instruction opLink) {
        this.opLink = opLink;
    }

    public Instruction getPrev() {
        return prev;
    }

    public void setPrev(Instruction prev) {
        this.prev = prev;
    }

    public Instruction getNext() {
        return next;
    }

    public void setNext(Instruction next) {
        this.next = next;
    }
}
