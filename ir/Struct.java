package ir;

public record Struct(Kind kind, int size, int n, Struct elemType) {

    enum Kind {
        None, Int, Arr
    }

    Struct(Kind kind) {
        this(kind, 0, 0, null);
    }

}
