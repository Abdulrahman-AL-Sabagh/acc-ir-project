package ir;

import java.util.List;

public record Block(int id, BlockKind kind, Block left, Block right, List<Block> pred, List<Instruction> instructions) {
    enum BlockKind {
        LOOP,
        CONDITION,
        NORMAL
    }

    Instruction addInstruction(Node x, Code.OpCode opCode, Node y) {
        var instruction = new Instruction(x, opCode, y);
        this.instructions.add(instruction);
        return instruction;

    }
    static int idGenerator = 0;
    public Block(BlockKind kind) {
        this(idGenerator++, kind, null,null, List.of(), List.of());
    }
}
