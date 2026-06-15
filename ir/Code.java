package ir;

import java.util.List;

public class Code {
    private byte[] code = new byte[3000];
    public int pc = 0;

    public enum OpCode {
        neg,
        plus,
        minus, var,
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
        nop,
        jmp,
        st,
        ble;


        public static Code.OpCode fjump(Code.OpCode op) {
            var val = bne;
            switch (op) {
                case beq -> val = bne;
                case bne -> val = beq;
                case bge -> val = blt;
                case ble -> val = bgt;
                case bgt -> val = ble;
                case blt -> val = bge;

                default -> {
                    throw new IllegalArgumentException("Can not get the false jump of a non-jump command");
                }
            }
            return val;
        }


    }

    public final static List<OpCode> jumpCommands =
            List.of(
                    OpCode.bgt,
                    OpCode.bge,
                    OpCode.bne,
                    OpCode.beq,
                    OpCode.blt,
                    OpCode.ble

            );


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


}
