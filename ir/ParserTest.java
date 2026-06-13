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
        Assertions.assertEquals(6, parser.cfg.getInstructions().size());
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
        Assertions.assertEquals(parser.cfg.getLeft().getKind(), Block.BlockKind.IF);
        Assertions.assertFalse(parser.cfg.getLeft().getInstructions().isEmpty());
        Assertions.assertNotNull(parser.cfg.getRight());
        Assertions.assertTrue(parser.cfg.getRight().getKind() == Block.BlockKind.NORMAL);
        Assertions.assertFalse(parser.cfg.getRight().getInstructions().isEmpty());
        Assertions.assertTrue(parser.cfg.getLeft().getRight() == parser.cfg.getRight());
    }

    @Test
    void testTheResultOfCFGWithIfElse_FromSlide33_ChapterIntermediateRepresentation() {

        var parser = constructParserForTestCase("""
                  fn main() {
                                      var x: int;
                                      var y: int;
                                      if (x > 0) { y = 1; }
                                      elseif (x < 0) { y = -1; }
                                      else { y = 0; }
                  }
                """);
        parser.Parse();
        var cfg = parser.cfg;
        System.out.println(cfg.toGraphViz());
        Assertions.assertTrue(cfg.getKind() == Block.BlockKind.NORMAL);
        Assertions.assertFalse(cfg.getInstructions().isEmpty());

        Assertions.assertNotNull(cfg.getLeft());
        Assertions.assertNotNull(cfg.getRight());

        Assertions.assertNotNull(cfg.getLeft().getRight());
        Assertions.assertNotNull(cfg.getRight().getLeft());
        Assertions.assertNotNull(cfg.getRight().getRight());
    }

    @Test
    void ensure_that_spilt_and_fix_works_correctly_with_while_loop_given_the_example_from_the_slides() {
        var parser = constructParserForTestCase("""
                                      fn main() {
                                        var a: int;
                                        a = 10;
                                        while (a > 0) { a = a - 1 ; }
                                      }
                """);
        parser.Parse();

        var cfg = parser.cfg;
        System.out.println(cfg.toGraphViz());
        Assertions.assertTrue(cfg.getInstructions().size() > 1);
        Assertions.assertNotNull(cfg.getLeft());
        Assertions.assertNotNull(cfg.getLeft().getRight());
        var whileCondition = cfg.getLeft();
        Assertions.assertSame(Block.BlockKind.WHILE, whileCondition.getKind());
        Assertions.assertSame(whileCondition.getLeft().getRight(), whileCondition);
    }
}