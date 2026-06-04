package ir;

import java.util.UUID;

public class Node {
    private int n;
    private static int counter = 1;

    public Node() {
        this.n = counter++;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

}
