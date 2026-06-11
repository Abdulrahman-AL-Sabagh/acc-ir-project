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
    void ensureThatIfStatementCanGenerateASplitAndFixup() {
        var parser = constructParserForTestCase("""
                    fn main() {
                        var a: int;
                        var b: int;
                        a = 3;
                        b = 3 * a;
                
                        if (a > b) {
                          a = a - b;
                        }
                        a = a + 1;
                    }
                """);
        parser.Parse();
        System.out.println(parser.cfg.toGraphViz());
        Assertions.assertNotNull(parser.cfg.getLeft());
        Assertions.assertEquals(parser.cfg.getLeft().getKind(), Block.BlockKind.CONDITION);
        Assertions.assertFalse(parser.cfg.getLeft().getInstructions().isEmpty());
        Assertions.assertNotNull(parser.cfg.getRight());
        Assertions.assertTrue(parser.cfg.getRight().getKind() == Block.BlockKind.NORMAL);
        Assertions.assertFalse(parser.cfg.getRight().getInstructions().isEmpty());
    }
}