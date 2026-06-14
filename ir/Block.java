package ir;

import java.util.*;

public class Block extends Node {

    // related to  the Graphviz project that is used with the testcases
    private static final boolean ENABLE_GRAPH_VIZ_GENERATION = true;
    private Block dom;
    private final List<Block> domChildren;
    private Block link;
    private HashMap<String, Node> value = new HashMap<>();

    enum BlockKind {
        WHILE,
        WHILE_BODY,
        IF,
        ELSE_IF,
        ELSE_IF_BODY,
        ELSE,
        NORMAL


    }

    private int id;
    private BlockKind kind;
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
        instructions.add(new Instruction(null, Code.OpCode.nop, null, this));
        this.domChildren = new ArrayList<>();
    }


    /**
     *
     * The Slides of Intermediate representation Part 1  provide a solution where this is probably implemeneted in the parser
     * However this functionality itself fits here in this class
     * To avoid confusions with the slides I will set the curBlock in the parser
     * to the return value of this function
     *
     * @return {@link Block}
     */


    Optional<Block> split(BlockKind kind) {
        if (this.instructions.getFirst() != this.instructions.getLast()
                && this.instructions.getLast().getOpCode() != Code.OpCode.jmp
        ) {
            Block b = new Block(kind);
            this.setLeft(b);
            b.pred.add(this);
            b.setDom(this);
            b.setValue((HashMap<String, Node>) value.clone());
            this.setLink(b);
            return Optional.of(b);
        }
        return Optional.empty();
    }

    static int idGenerator = 0;

    public Block(BlockKind kind) {
        this(idGenerator++, kind, null, null, new LinkedList<>(), new LinkedList<>());
    }


    @Override
    public String toString() {
        var string = new StringBuilder();
        for (Instruction instruction : this.instructions) {
            string.append(instruction).append("\n");
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
        var blockName = generateBlockName(block);
        sb.append(blockName).append(" ");
        sb.append("[ label=<").append("\n").append("<table> \n");
        sb.append("<tr><td><b>").append(blockName).append("</b></td></tr>\n");
        for (Instruction instruction : block.getInstructions()) {
            sb.append("<tr><td>").append(instruction.toString()).append("</td></tr>").append("\n");
        }
        sb.append("</table> \n").append(" >]").append("\n");
        sb.append("\n");
    }

    private String generateBlockName(Block block) {
        return String.format("%s_%d ", block.kind, block.id);
    }

    private void dfsBlock(Block block, StringBuilder sb, HashSet<Block> visited) {
        serializeBlock(block, sb);

        if (block.getLeft() != null) {
            String edgString = generateBlockName(block) + " -> " + generateBlockName(block.getLeft());
            if (visited.contains(block.getLeft())) {
                sb.append(edgString).append("\n");
                return;
            }
            visited.add(block.getLeft());
            dfsBlock(block.getLeft(), sb, visited);
            sb.append(generateBlockName(block)).append("->").append(generateBlockName(block.getLeft())).append("\n");
        }
        if (block.getRight() != null) {
            String edgString = generateBlockName(block) + " -> " + generateBlockName(block.getRight());

            if (visited.contains(block.getRight())) {
                sb.append(edgString).append("\n");
                return;
            }
            visited.add(block.getRight());
            dfsBlock(block.getRight(), sb, visited);
            sb.append(generateBlockName(block)).append("->").append(generateBlockName(block.getRight())).append("\n");
        }
    }

    public String toGraphViz() {
        StringBuilder sb = new StringBuilder();

        sb.append("digraph G {\n");
        sb.append("node [shape=box];\n");
        var visited = new HashSet<Block>();

        dfsBlock(this, sb, visited);

        sb.append("}");
        return sb.toString();
    }

    private void toDominatorTreeString(Block current, StringBuilder sb) {
        var currentName = generateBlockName(current);
        for (Block b : current.getDomChildren()) {
            toDominatorTreeString(b, sb);
            var bName = generateBlockName(b);
            serializeBlock(b, sb);
            sb.append(bName).append(" -> ").append(currentName).append("[dir=back ]\n");
        }
    }


    public String dominatorTreeToGraphViz() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph DominatorTree {\n");
        sb.append("node [shape=box];\n");
        toDominatorTreeString(this, sb);
        sb.append("}");
        return sb.toString();

    }

    public Block getDom() {
        return dom;
    }

    public void setDom(Block dom) {
        this.dom = dom;
    }

    public List<Block> getDomChildren() {
        return domChildren;
    }

    public Block getLink() {
        return link;
    }

    public void setLink(Block link) {
        this.link = link;
    }

    public void setKind(BlockKind kind) {
        this.kind = kind;
    }

    public HashMap<String, Node> getValue() {
        return value;
    }

    public void setValue(HashMap<String, Node> value) {
        this.value = value;
    }
}
