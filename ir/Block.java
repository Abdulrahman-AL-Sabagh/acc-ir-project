package ir;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Block {

    // related to  the Graphviz project that is used with the testcases
    private static final boolean ENABLE_GRAPH_VIZ_GENERATION = true;

    enum BlockKind {
        LOOP,
        CONDITION,
        NORMAL;


    }

    private int id;
    private final BlockKind kind;
    private Block left;
    private Block right;
    private List<Block> pred;
    private List<Instruction> instructions;


    private Block(int id, BlockKind kind, Block left, Block right, List<Block> pred, List<Instruction> instructions) {
        this.id = id;
        this.kind = kind;
        this.left = left;
        this.right = right;
        this.pred = pred;
        this.instructions = instructions;
    }


    /**
     *
     * The Slides of Intermediate representation Part 1  provide a solution where this is probably implemeneted in the parser
     * However this functionality itself fits here in this class
     * To avoid confusions with the slides I will set the curBlock in the parser
     * to the return value of this function
     *
     * @param kind
     * @return {@link Block}
     */
    Optional<Block> split(BlockKind kind) {
        if (this.instructions.getFirst() != this.instructions.getLast()
            //&& !Code.jumpCommands.contains(this.instructions.getLast().getOpCode())
        ) {
            Block b = new Block(kind);
            this.setLeft(b);
            b.pred.add(this);
            return Optional.of(b);
        }
        return Optional.empty();
    }


    static void fixup(Instruction i, Block currBlock) {
        i.setY(currBlock.getInstructions().getFirst());
        i.getBlock().setRight(currBlock);
        currBlock.pred.add(i.getBlock());
    }

    Instruction addInstruction(Node x, Code.OpCode opCode, Node y) {

        var instruction = new Instruction(x, opCode, y, this);
        this.instructions.add(instruction);
        return instruction;

    }


    static int idGenerator = 0;

    public Block(BlockKind kind) {
        this(idGenerator++, kind, null, null, new LinkedList<>(), new LinkedList<>());
    }


    @Override
    public String toString() {
        var string = new StringBuilder();
        for (Instruction instruction : this.instructions) {
            string.append(String.format("%d. ", instruction.getId())).append(instruction.toString()).append("\n");
        }
        return string.toString();
    }

    public Block getLeft() {
        return left;
    }

    public void setLeft(Block left) {
        this.left = left;
    }

    public Block getRight() {
        return right;
    }

    public void setRight(Block right) {
        this.right = right;
    }

    public List<Block> getPred() {
        return pred;
    }

    public void setPred(List<Block> pred) {
        this.pred = pred;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
    }

    public static int getIdGenerator() {
        return idGenerator;
    }

    public static void setIdGenerator(int idGenerator) {
        Block.idGenerator = idGenerator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BlockKind getKind() {
        return kind;
    }

    // Code constructed in a similar structure to this here
    // https://graphviz.org/Gallery/directed/psg.html
    public void serializeBlock(Block block, StringBuilder sb) {

        sb.append(generateBlockName(block)).append(" ");
        sb.append("[ label=<").append("\n").append("<table> \n");
        for (Instruction instruction : block.getInstructions()) {
            sb.append("<tr><td>").append(instruction.toString()).append("</td></tr>").append("\n");
        }
        sb.append("</table> \n").append(" >]").append("\n");
        sb.append("\n");
    }

    private String generateBlockName(Block block) {
        return String.format("%s_%d ", block.kind, block.id);
    }

    private void dfsBlock(Block block, StringBuilder sb) {
        serializeBlock(block, sb);

        if (block.getLeft() != null) {
            dfsBlock(block.getLeft(), sb);
            sb.append(generateBlockName(block)).append("->").append(generateBlockName(block.getLeft())).append("\n");
        }
        if (block.getRight() != null) {
            dfsBlock(block.getRight(), sb);
            sb.append(generateBlockName(block)).append("->").append(generateBlockName(block.getRight())).append("\n");
        }
    }

    public String toGraphViz() {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph G {\n");
        sb.append("node [shape=box];\n");
        dfsBlock(this, sb);

        sb.append("}");
        return sb.toString();
    }
}
