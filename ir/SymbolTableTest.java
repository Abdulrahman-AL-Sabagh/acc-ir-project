package ir;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class SymbolTableTest {
    private Parser parser;

    @Test
    void testIfIntAndMainExistByDefault() {
        parser = new Parser(null);

        assertTrue(parser.getSymbolTable().currScope.getLocals().containsKey("main"));


    }

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
    void redefiningMainShouldThrow() {
        parser = new Parser(new Scanner(
                new ByteArrayInputStream("""
                                var main: int;
                                var a: int;
                                fn main() {}
                        """.getBytes())));
        assertThrows(RuntimeException.class, () -> parser.Parse());


    }
}