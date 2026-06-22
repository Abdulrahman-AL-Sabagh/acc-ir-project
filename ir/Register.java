package ir;

public class Register extends Node {
    final String name;
    int addr;
    public Register(String name, int addr) {
        this.name = name;
        this.addr = addr;
    }
    @Override
    public String toString() {
        return name;
    }
}
