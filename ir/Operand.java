package ir;

/**
 * Since there the given language has only one type, then there is actually on need for the field *type* (W3 Slide 9)
 *
 * @param kind
 * @param val
 * @param reg
 * @param adr
 * @param inx
 * @param scale
 */
public record Operand(

        int kind,
        int val,
        int reg,
        int adr,
        int inx,
        int scale
) {
}
