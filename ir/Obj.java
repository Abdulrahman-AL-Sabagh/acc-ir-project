package ir;

import java.util.HashMap;
import java.util.Map;

public class Obj extends Node {
    private int id;
    private static int idGenerator;
    final String name;
    final Kind kind;
    private final Struct type;
    int val;
    int adr;
    int level;
    int nVars;
    int num;
    Map<String, Obj> locals;

    public Obj(String name, Kind kind, int level, int num) {
        this.name = name;
        this.kind = kind;
        this.locals = new HashMap<>();
        if (kind != Kind.Type) {
            this.type = SymbolTable.intType;
        } else {
            this.type = null;
        }
        this.id = idGenerator++;
        this.num = num;
        this.level = level;
    }


    enum Kind {
        Var,
        Type,
        Meth // Actually only for main, since this langauge has only a main method and nothing else.
        ;
        /**
         * This constructor should only produce type objects. In my case it's about arrays and integer arrays
         *
         * @param name
         */


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return String.format("%s", this.name);
    }
}
