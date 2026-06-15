package ir;

public record Struct(Kind kind, int length ,Struct elemType) {



    enum Kind {
        None, Int, Arr
    }

    Struct(Kind kind) {
        this(kind, 0, null);
    }


}