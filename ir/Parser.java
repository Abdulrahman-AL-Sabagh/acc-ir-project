
package ir;

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
    private SymbolTable symbolTable = new SymbolTable();
    public static final Code code = new Code();
    public Block cfg;
    public Block curBlock;


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
        Expect(10);
        Expect(11);
        Expect(12);
        symbolTable.openScope();
        cfg = new Block(Block.BlockKind.NORMAL);
        curBlock = cfg;
        while (la.kind == 3) {
            VarDecl();
        }
        StatSeq();
        Expect(13);
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
        if (la.kind == 1) {
            Node x, y;
            x = Designator(false);
            Expect(14);
            y = Expression();
            curBlock.addInstruction(x, Code.OpCode.ass, y);
            Expect(5);
        } else if (la.kind == 15) {
            Get();
            Expect(10);
            Condition();
            Expect(11);
            Expect(12);
            StatSeq();
            Expect(13);
            while (la.kind == 16) {
                Get();
                Expect(10);
                Condition();
                Expect(11);
                Expect(12);
                StatSeq();
                Expect(13);
            }
            if (la.kind == 17) {
                Get();
                Expect(12);
                StatSeq();
                Expect(13);
            }
        } else if (la.kind == 18) {
            Get();
            Expect(10);
            Condition();
            Expect(11);
            Expect(12);
            StatSeq();
            Expect(13);
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
            x = curBlock.addInstruction(x, op, null);
        }
        while (la.kind == 27 || la.kind == 28) {
            op = Addop();
            y = Term();
            x = curBlock.addInstruction(x, op, y);
        }
        return x;
    }

    void Condition() {
        Expression();
        Relop();
        Expression();
    }

    void Relop() {
        switch (la.kind) {
            case 21: {
                Get();
                break;
            }
            case 22: {
                Get();
                break;
            }
            case 23: {
                Get();
                break;
            }
            case 24: {
                Get();
                break;
            }
            case 25: {
                Get();
                break;
            }
            case 26: {
                Get();
                break;
            }
            default:
                SynErr(34);
                break;
        }
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
            x = curBlock.addInstruction(x, op, y);
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
            Expect(11);
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
