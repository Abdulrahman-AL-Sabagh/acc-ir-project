package ir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTableTest {
    private Parser parser;

/*    @Test
    void testIfIntAndMainExistByDefault() {
        parser = new Parser(null);

        assertTrue(parser.getSymbolTable().currScope.getLocals().containsKey("main"));


    }*/

    @Test
    void testIfGlobalVariablesAreAdded() {
        parser = new Parser(new Scanner(
                new BufferedInputStream(
                        new ByteArrayInputStream("""
                                var a: int;
                                var b: int;
                                fn main() {
                                """.getBytes())
                )
        ));
        parser.Parse();
        assertTrue(parser.getSymbolTable().currScope.getLocals().containsKey("a"));
        assertTrue(parser.getSymbolTable().currScope.getLocals().containsKey("b"));
        assertNotNull(parser.getSymbolTable().currScope.getOuter());
        assertTrue(parser.getSymbolTable().curLevel > -1);
        System.out.println(parser.getSymbolTable());
        assertEquals(-8, SymbolTable.SP);

    }

    @Test
    void exceptionIsThrownIfVariableIsDefindTwice() {
        parser = new Parser(new Scanner(
                new ByteArrayInputStream("""
                                var a: int;
                                var a: int;
                        """.getBytes())


        ));

        assertThrows(RuntimeException.class, () -> parser.Parse());
    }

    @Test
    void check_if_table_can_handle_arrays() {
        var parser = new Parser(new Scanner(
                new ByteArrayInputStream("""
                        var a: int;
                        var b: int;
                        var c: int[10];
                        
                        fn main() {
                        """.getBytes())
        ));
        parser.Parse();
        var t = parser.getSymbolTable();
        var c = t.find("c");

        Assertions.assertNotNull(t.find("a"));
        Assertions.assertNotNull(t.find("b"));
        Assertions.assertNotNull(c);
        Assertions.assertSame(Struct.Kind.Arr, c.getType().kind());

        Assertions.assertEquals(-48, SymbolTable.SP);
    }

 /*   @Test
    void redefiningMainShouldThrow() {
        parser = new Parser(new Scanner(
                new ByteArrayInputStream("""
                                var main: int;
                                var a: int;
                                fn main() {}
                        """.getBytes())));
        assertThrows(RuntimeException.class, () -> parser.Parse());


    }*/
}