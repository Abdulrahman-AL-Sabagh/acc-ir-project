
package ir;

import ir.Block.BlockKind;

import java.util.*;

import static ir.Block.BlockKind.*;

public class Parser {
    public static final int _EOF = 0;
    public static final int _ident = 1;
    public static final int _number = 2;
    public static final int maxT = 32;

    static final boolean _T = true;
    static final boolean _x = false;
    static final int minErrDist = 2;

    public Token t;    // last recognized token
    public Token la;   // lookahead token
    int errDist = minErrDist;

    public Scanner scanner;
    public Errors errors;
    private final SymbolTable symbolTable = new SymbolTable();
    public static final Code code = new Code();
    public Block cfg;
    public Block curBlock;

    void genPhi(Obj var) {
        Instruction i = gen(var, Code.OpCode.phi, null);
        curBlock.getValue().put(var.name, i);

    }

    private List<Block> getPathFromBlockToRoot(Block block) {
        var path = new HashSet<Block>();
        var current = block;
        while (current != null) {
            path.add(current);
            current = current.getDom();
        }
        return path.stream().toList();
    }

    //TODO : IMPLEMENT THIS
    Block getCommonDom(Block p, Block q) {
        if (p == null) return q;
        else {
            var domListOfP = getPathFromBlockToRoot(p);
            var domListOfQ = new ArrayList<>(getPathFromBlockToRoot(q));
            var differences = domListOfQ.retainAll(domListOfP);
            System.out.println(differences);
            return domListOfQ.getFirst();

        }
    }

    void genPhis() {
        curBlock.setValue((HashMap<String, Node>) curBlock.getPred().getFirst().getValue().clone());
        var locals = symbolTable.currScope.getLocals();
        for (var local : locals.keySet()) {
            for (Block pred : curBlock.getPred()) {
                if (curBlock.getValue().get(local) != pred.getValue().get(local)) {
                    genPhi(locals.get(local));
                    break;
                }
            }
        }

        curBlock.setDom(null);
        for (Block b : curBlock.getPred()) {
            curBlock.setDom(getCommonDom(curBlock.getDom(), b));
        }
    }

    void genJump(Block b) {
        gen(null, Code.OpCode.jmp, b.getInstructions().getFirst());
        curBlock.setRight(b);
        b.getPred().add(curBlock);
    }

    void linkWithLeft(Block b) {
        curBlock.setLeft(b);
        b.getPred().add(curBlock);
        curBlock.setLink(b);
    }


    void invertDomTree() {
        for (Block b = cfg; b != null; b = b.getLink()) {
            if (b.getDom() != null) {
                b.getDom().getDomChildren().add(b);
            }
        }
    }

    Node cur(Node x) {
        if (!(x instanceof Obj var)) return x;
        if (var.level == symbolTable.curLevel) x = curBlock.getValue().get(var.name);
        return x;
    }

    void genAssign(Obj var, Node y) {
        Instruction i = new Instruction(var, Code.OpCode.ass, cur(y), curBlock);
        curBlock.getInstructions().add(i);
        if (var.level == symbolTable.curLevel) curBlock.getValue().put(var.name, i);
    }

    private Instruction gen(Node x, Code.OpCode op, Node y) {
        var instr = new Instruction(cur(x), op, cur(y), curBlock);
        curBlock.getInstructions().add(instr);
        return instr;
    }

    private void fixup(Instruction i) {
        i.setY(curBlock.getInstructions().getFirst());
        i.getBlock().setRight(curBlock);
        curBlock.getPred().add(i.getBlock());
        curBlock.setDom(i.getBlock());
        curBlock.setValue((HashMap<String, Node>) i.getBlock().getValue().clone());
    }

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        errors = new Errors();
    }

    void SynErr(int n) {
        if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
        errDist = 0;
    }

    public void SemErr(String msg) {
        if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
        errDist = 0;
    }

    void Get() {
        for (; ; ) {
            t = la;
            la = scanner.Scan();
            if (la.kind <= maxT) {
                ++errDist;
                break;
            }

            la = t;
        }
    }

    void Expect(int n) {
        if (la.kind == n) Get();
        else {
            SynErr(n);
        }
    }

    boolean StartOf(int s) {
        return set[s][la.kind];
    }

    void ExpectWeak(int n, int follow) {
        if (la.kind == n) Get();
        else {
            SynErr(n);
            while (!StartOf(follow)) Get();
        }
    }

    boolean WeakSeparator(int n, int syFol, int repFol) {
        int kind = la.kind;
        if (kind == n) {
            Get();
            return true;
        } else if (StartOf(repFol)) return false;

        else {
            SynErr(n);
            while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
                Get();
                kind = la.kind;
            }
            return StartOf(syFol);
        }
    }

    void MiniLang() {
        while (la.kind == 3) {
            VarDecl();
        }
        MainDecl();
    }

    void VarDecl() {
        Expect(3);

        Expect(1);
        var name = t.val;
        Expect(4);
        Type();
        Expect(5);
        System.out.println(name);
        symbolTable.insert(name, Obj.Kind.Var);
    }

    void MainDecl() {
        Expect(8);
        Expect(9);
        Expect(10); // "("
        Expect(11); // ")"
        Expect(12); // "{"
        symbolTable.openScope();
        cfg = new Block(NORMAL);
        curBlock = cfg;
        while (la.kind == 3) {
            VarDecl();
        }
        StatSeq();
        Expect(13); // "}"
        symbolTable.closeScope();
    }

    void Type() {
        Expect(1);
        while (la.kind == 6) {
            Get();
            Expect(2);
            Expect(7);
        }
    }

    void StatSeq() {
        Statement();
        while (StartOf(1)) {
            Statement();
        }
    }

    void Statement() {
        Label l = new Label(null, null, null);

        if (la.kind == 1) {
            Obj x;
            Node y;
            x = (Obj) Designator(false);
            Expect(14);
            y = Expression();
            genAssign(x, y);
            Expect(5);
        } else if (la.kind == 15) { // "if"
            Get();
            Expect(10); // "("
            Condition(l);
            l.setFix(gen(l.getCond(), Code.OpCode.fjump(l.getOp()), null));
            curBlock = curBlock.split(IF).orElse(curBlock);

            Expect(11); // ")"
            Expect(12); // "{"
            StatSeq();
            Expect(13); // "}"
            Block join = new Block(NORMAL);
            /*
            if (la.kind != 16 && la.kind != 17) {
                gen(null, Code.OpCode.jmp, join.getInstructions().getFirst());
                curBlock.setRight(join);
                fixup(l.getFix());
                l.getFix().getBlock().setRight(join);
                curBlock.setLink(join);
                curBlock = curBlock.getLink();
                return;
            }
*/
            while (la.kind == 16) { // "elseif"
                Get();
                Expect(10); // "("
                // TOOD: Decide later how to get rid of this jmp command so it matches the expected output in the slides
                // curBlock.addInstruction(null, Code.OpCode.jmp, join.getInstructions().getFirst());
                genJump(join);
                curBlock.setLink(new Block(ELSE_IF));
                curBlock = curBlock.getLink();
                fixup(l.getFix());
                Condition(l);
                l.setFix(gen(l.getCond(), Code.OpCode.fjump(l.getOp()), null));
                curBlock = curBlock.split(ELSE_IF_BODY).orElse(curBlock);
                Expect(11); // ")"
                Expect(12); // "{"
                StatSeq();
                Expect(13); // "}"
            }
            genJump(join);
           /* gen(null, Code.OpCode.jmp, join.getInstructions().getFirst());
            curBlock.setRight(join);
            var b = new Block(NORMAL);
            curBlock.setLink(b);
            curBlock = curBlock.getLink();*/
            curBlock.setLink(new Block(NORMAL));
            curBlock = curBlock.getLink();
            fixup(l.getFix());
            if (la.kind == 17) { // "else"
                curBlock.setKind(ELSE);
                Get();
                Expect(12); // "{"
                StatSeq();
                Expect(13); // "}"
            }

            linkWithLeft(join);
            curBlock = join;
            genPhis();

        } else if (la.kind == 18) { // "while"
            Get();
            Expect(10); // "("

            var splitResult = curBlock.split(Block.BlockKind.WHILE);
            if (splitResult.isPresent()) {
                curBlock.setLink(splitResult.get());
                curBlock = curBlock.getLink();
            }
            Block join = curBlock;
            Condition(l);

            Expect(11); // ")"
            Expect(12); // "{"
            l.setFix(gen(l.getCond(), Code.OpCode.fjump(l.getOp()), null));
            splitResult = curBlock.split(BlockKind.WHILE_BODY);
            if (splitResult.isPresent()) {
                curBlock.setLink(splitResult.get());
                curBlock = curBlock.getLink();
            }
            StatSeq();

            Expect(13); // "}"
            gen(null, Code.OpCode.jmp, join.getInstructions().getFirst());
            curBlock.setRight(join);
            curBlock.setLink(new Block(NORMAL));
            curBlock = curBlock.getLink();
            fixup(l.getFix());
        } else if (la.kind == 19) {
            Get();
            Designator(false);
            Expect(5);
        } else if (la.kind == 20) {
            Get();
            Expression();
            Expect(5);
        } else SynErr(33);
    }

    Node Designator(boolean load) {
        Expect(1);
        var varName = t.val;
        var x = symbolTable.find(varName);
        while (la.kind == 6) {
            Get();
            Expression();
            Expect(7);
        }
        return x;
    }

    Node Expression() {
        var op = Code.OpCode.plus;
        Node x, y;

        if (la.kind == 27 || la.kind == 28) {
            op = Addop();
        }
        x = Term();
        if (op == Code.OpCode.minus) {
            op = Code.OpCode.neg;
            x = gen(x, op, null);
        }
        while (la.kind == 27 || la.kind == 28) {
            op = Addop();
            y = Term();
            x = gen(x, op, y);
        }
        return x;
    }

    /**
     *
     * @param label
     */
    void Condition(Label label) {
        var lhs = Expression();
        var op = Relop();
        var rhs = Expression();
        label.setOp(op);
        var compareInstruction = gen(lhs, op, rhs);
        label.setCond(compareInstruction);
    }

    Code.OpCode Relop() {
        var op = Code.OpCode.beq;
        switch (la.kind) {
            case 21: {
                Get();
                op = Code.OpCode.beq;
                break;
            }
            case 22: {
                Get();
                op = Code.OpCode.bne;
                break;
            }
            case 23: {
                Get();
                op = Code.OpCode.blt;
                break;
            }
            case 24: {
                Get();
                op = Code.OpCode.bgt;
                break;
            }
            case 25: {
                Get();
                op = Code.OpCode.bge;
                break;
            }
            case 26: {
                Get();
                op = Code.OpCode.ble;
                break;
            }
            default:
                SynErr(34);
                break;
        }
        return op;
    }

    Code.OpCode Addop() {
        Code.OpCode op = Code.OpCode.plus;
        if (la.kind == 27) {
            Get();
        } else if (la.kind == 28) {
            Get();
            op = Code.OpCode.minus;
        } else SynErr(35);
        return op;
    }

    Node Term() {
        var x = Factor();
        while (la.kind == 29 || la.kind == 30 || la.kind == 31) {
            var op = Mulop();
            var y = Factor();
            x = gen(x, op, y);
        }
        return x;
    }

    Node Factor() {
        Node x = null;
        if (la.kind == 1) {
            x = Designator(true);
        } else if (la.kind == 2) {
            Get();
            x = new Constant(Integer.parseInt(t.val));
        } else if (la.kind == 10) {
            Get();
            x = Expression();
            Expect(11); // ")"
        } else SynErr(36);
        return x;
    }

    Code.OpCode Mulop() {
        Code.OpCode op = Code.OpCode.times;
        if (la.kind == 29) {
            Get();
        } else if (la.kind == 30) {
            Get();
            op = Code.OpCode.div;
        } else if (la.kind == 31) {
            Get();
            op = Code.OpCode.rem;
        } else SynErr(37);
        return op;
    }


    public void Parse() {
        la = new Token();
        la.val = "";
        Get();
        MiniLang();
        Expect(0);

        scanner.buffer.Close();
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    private static final boolean[][] set = {
            {_T, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x},
            {_x, _T, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _T, _x, _x, _T, _T, _T, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x}

    };
} // end Parser


class Errors {
    public int count = 0;                                    // number of errors detected
    public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
    public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text

    protected void printMsg(int line, int column, String msg) {
        StringBuffer b = new StringBuffer(errMsgFormat);
        int pos = b.indexOf("{0}");
        if (pos >= 0) {
            b.delete(pos, pos + 3);
            b.insert(pos, line);
        }
        pos = b.indexOf("{1}");
        if (pos >= 0) {
            b.delete(pos, pos + 3);
            b.insert(pos, column);
        }
        pos = b.indexOf("{2}");
        if (pos >= 0) b.replace(pos, pos + 3, msg);
        errorStream.println(b.toString());
    }

    public void SynErr(int line, int col, int n) {
        String s;
        switch (n) {
            case 0:
                s = "EOF expected";
                break;
            case 1:
                s = "ident expected";
                break;
            case 2:
                s = "number expected";
                break;
            case 3:
                s = "\"var\" expected";
                break;
            case 4:
                s = "\":\" expected";
                break;
            case 5:
                s = "\";\" expected";
                break;
            case 6:
                s = "\"[\" expected";
                break;
            case 7:
                s = "\"]\" expected";
                break;
            case 8:
                s = "\"fn\" expected";
                break;
            case 9:
                s = "\"main\" expected";
                break;
            case 10:
                s = "\"(\" expected";
                break;
            case 11:
                s = "\")\" expected";
                break;
            case 12:
                s = "\"{\" expected";
                break;
            case 13:
                s = "\"}\" expected";
                break;
            case 14:
                s = "\"=\" expected";
                break;
            case 15:
                s = "\"if\" expected";
                break;
            case 16:
                s = "\"elseif\" expected";
                break;
            case 17:
                s = "\"else\" expected";
                break;
            case 18:
                s = "\"while\" expected";
                break;
            case 19:
                s = "\"read\" expected";
                break;
            case 20:
                s = "\"write\" expected";
                break;
            case 21:
                s = "\"==\" expected";
                break;
            case 22:
                s = "\"!=\" expected";
                break;
            case 23:
                s = "\"<\" expected";
                break;
            case 24:
                s = "\">\" expected";
                break;
            case 25:
                s = "\">=\" expected";
                break;
            case 26:
                s = "\"<=\" expected";
                break;
            case 27:
                s = "\"+\" expected";
                break;
            case 28:
                s = "\"-\" expected";
                break;
            case 29:
                s = "\"*\" expected";
                break;
            case 30:
                s = "\"/\" expected";
                break;
            case 31:
                s = "\"%\" expected";
                break;
            case 32:
                s = "??? expected";
                break;
            case 33:
                s = "invalid Statement";
                break;
            case 34:
                s = "invalid Relop";
                break;
            case 35:
                s = "invalid Addop";
                break;
            case 36:
                s = "invalid Factor";
                break;
            case 37:
                s = "invalid Mulop";
                break;
            default:
                s = "error " + n;
                break;
        }
        printMsg(line, col, s);
        count++;
    }

    public void SemErr(int line, int col, String s) {
        printMsg(line, col, s);
        count++;
    }

    public void SemErr(String s) {
        errorStream.println(s);
        count++;
    }

    public void Warning(int line, int col, String s) {
        printMsg(line, col, s);
    }

    public void Warning(String s) {
        errorStream.println(s);
    }
} // Errors


class FatalError extends RuntimeException {
    public static final long serialVersionUID = 1L;

    public FatalError(String s) {
        super(s);
    }
}
