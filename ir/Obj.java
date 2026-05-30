package ir;

import java.util.Map;

public record Obj(String name, Struct type, int val, int adr, int level, int nVars, int nPars, Map<String, Obj> locals) {

}
