package ir;

public class Node {
    private int id;
    private static int counter = 1;

    public Node() {
        this.id = counter++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
