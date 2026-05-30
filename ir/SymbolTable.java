package ir;

import java.util.Map;

public class SymbolTable {

    record Scope(Scope outer, Map<String, Obj> locals, int nVars) {

    }

}
