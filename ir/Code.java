package ir;

public class Code {
    private byte[] code = new byte[3000];
    public int pc = 0;
    public enum OpCode {
        neg,
        plus,
        minus,
        times,
        div,
        rem,
        cmp,
        phi,
        ld,
        lr,
        lc,
        ass,
        read,
        write,
        ret,
        br,
        blt,
        beq,
        bgt,
        bge,
        bne,
        ble

    }
    public void Put(int x) {
        code[pc++] = (byte) x;
    }

    public void Put2(int x) {
        Put(x);
        Put(x >> 8);
    }

    public void Put4(int x) {
        Put(x);
        Put2(x >> 16);
    }

    public void PutConst(int size, int x) {
        if (size == 1) Put(x);
        else if (size == 2) Put2(x);
        else Put4(x);
    }

}
