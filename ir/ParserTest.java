package ir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ParserTest {


    private Parser constructParserForTestCase(String code) {
        return new Parser(new Scanner(new ByteArrayInputStream(code.getBytes())));
    }

    @Test
    void checkIfTheCodeOnSlide29ChapterIntermediateRepresentationPart1CanBeGeneratedCorrectly() {
        var parser = constructParserForTestCase(
                """
                        fn main() {
                        var a: int;
                        var b: int;
                        var c: int;
                        c = 2;
                        b = 1;
                        a = 3 + b * c;
                        
                        }
                        """
        );

        parser.Parse();
        System.out.println(parser.cfg);
        Assertions.assertFalse(parser.cfg.getInstructions().isEmpty());
        Assertions.assertEquals(5, parser.cfg.getInstructions().size());
    }

    @Test
    void ensureThatIfStatementCanGenerateASplit() {
        var parser = constructParserForTestCase("""
                    fn main() {
                        var a: int;
                        var b: int;
                        a = 3;
                        b = 3 * a;
                
                        if (a > b) {
                          a = a - b;
                        } else {
                          write a;
                        }
                    }
                """);
        parser.Parse();
        Assertions.assertNotNull(parser.cfg.getLeft());
        Assertions.assertTrue(parser.cfg.getLeft().getKind().equals(Block.BlockKind.CONDITION));
    }
}