package ir;

import java.util.HashMap;

import java.util.Map;
import java.util.Objects;


public class SymbolTable {

    public static Struct intType = new Struct(Struct.Kind.Int, 1, null);
    Scope currScope = null;
    int curLevel = -2;
    static  Register SP = new Register("SP", 0)  ;
    static Register FP = new Register("FP", 0);

    class Scope {
        private Scope outer;
        private Map<String, Obj> locals;
        private int nVars;


        public Scope(Scope outer, Map<String, Obj> locals, int nVars) {
            this.outer = outer;
            this.locals = locals;
            this.nVars = nVars;

        }

        public Scope getOuter() {
            return outer;
        }

        public void setOuter(Scope outer) {
            this.outer = outer;
        }

        public Map<String, Obj> getLocals() {
            return locals;
        }

        public void setLocals(Map<String, Obj> locals) {
            this.locals = locals;
        }

        public int getnVars() {
            return nVars;
        }

        public void setnVars(int nVars) {
            this.nVars = nVars;
        }
    }


    public SymbolTable() {


        openScope();
        insert("int", Obj.Kind.Type,null);
        openScope();
        insert("main", Obj.Kind.Meth, null);

    }

    // LinkedHashMap<String, Scope> scopes = new LinkedHashMap<>();


    /**
     *
     * @param name
     * @param kind
     * @return
     */
    void insert(String name, Obj.Kind kind, Struct type) {
        if (find(name) != null) {
            throw new IllegalArgumentException("The variable " + name + "  is already defined");
        }
        Obj obj = null;
        switch (kind) {
            case Type -> obj = new Obj(name, Obj.Kind.Type, curLevel, currScope.nVars, -1, null);
            case Var -> {
                SP.addr -=  4 * type.length();
                obj = new Obj(name, kind, curLevel, currScope.nVars, SP.addr, type);
            }
            //  case Meth -> obj = new Obj(name, kind, new Struct(Struct.Kind.None), 0, 0, curLevel, 0, 0, new HashMap<>());
            default -> {
            }
        }
        if (obj != null) {
            this.currScope.locals.put(name, obj);
            currScope.setnVars(currScope.getnVars() + 1);
        }

    }

    Obj find(String name) {
        Scope curr = this.currScope;
        while (curr != null) {
            if (curr.locals.containsKey(name)) {
                return curr.locals.get(name);
            }
            curr = curr.outer;
        }
        return null;
    }

    void openScope() {
        this.currScope = new Scope(currScope, new HashMap<>(), 0);
        this.curLevel++;

    }

    void closeScope() {

        this.currScope = this.currScope.outer;
        this.curLevel--;
    }

    @Override
    public String toString() {
        return currScope.toString();
    }

    public int getCurLevel() {
        return curLevel;
    }
}
