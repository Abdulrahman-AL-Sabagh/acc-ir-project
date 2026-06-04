package ir;

import java.util.HashMap;
import java.util.Map;

public class Obj extends Node {
    final String name;
    final Kind kind;
    private final Struct type;
    int val;
    int adr;
    int level;
    int nVars;
    Map<String, Obj> locals;

    public Obj(String name, Kind kind) {
        this.name = name;
        this.kind = kind;
        this.locals = new HashMap<>();
        if (kind != Kind.Type) {
            this.type = SymbolTable.intType;
        } else {
            this.type = null;
        }
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

}
