package ir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;


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
        var cfg = parser.cfg;
        var generatedElseBlock = cfg.getRight();
        var ifBlock = cfg.getLeft();
        var joinBlock = cfg.getRight().getLeft();
        System.out.println(parser.cfg.toGraphViz());
        Assertions.assertNotNull(parser.cfg.getLeft());
        Assertions.assertEquals(Block.BlockKind.IF, parser.cfg.getLeft().getKind());
        Assertions.assertFalse(parser.cfg.getLeft().getInstructions().isEmpty());
        Assertions.assertNotNull(parser.cfg.getRight());
        Assertions.assertSame(Block.BlockKind.NORMAL, parser.cfg.getRight().getKind());
        Assertions.assertSame(Block.BlockKind.NORMAL, generatedElseBlock.getKind());
        Assertions.assertTrue( generatedElseBlock.getInstructions().isEmpty());
        Assertions.assertSame(generatedElseBlock.getLeft(), joinBlock);


        // TESTING correct order of predecessor
        Assertions.assertTrue(cfg.getPred().isEmpty());
        Assertions.assertEquals(1, ifBlock.getPred().size());
        Assertions.assertEquals(1, generatedElseBlock.getPred().size());
        Assertions.assertEquals(2, joinBlock.getPred().size());

        // TESTING the LINK order
        Assertions.assertEquals(cfg.getLink(), ifBlock);
        Assertions.assertEquals(ifBlock.getLink(), generatedElseBlock);
        Assertions.assertEquals(generatedElseBlock.getLink(), joinBlock);
        Assertions.assertNull(joinBlock.getLink());

        // TESTING PHI Existence
        Assertions.assertTrue(joinBlock.getInstructions().stream().anyMatch(i -> i.getOpCode().equals(Code.OpCode.phi)));
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
        var ifBlock = cfg.getLeft();
        var elseIfBlock = cfg.getRight();
        var elseIfBodyBlock = elseIfBlock.getLink();
        var elseBlock = elseIfBlock.getRight();
        var joinBlock = ifBlock.getRight();
        System.out.println(cfg.toGraphViz());
        Assertions.assertSame(Block.BlockKind.NORMAL, cfg.getKind());
        Assertions.assertFalse(cfg.getInstructions().isEmpty());

        Assertions.assertNotNull(cfg.getLeft());
        Assertions.assertNotNull(cfg.getRight());

        Assertions.assertNotNull(cfg.getLeft().getRight());
        Assertions.assertNotNull(cfg.getRight().getLeft());
        Assertions.assertNotNull(cfg.getRight().getRight());

        // TESTING LINK Order
        Assertions.assertEquals(cfg.getLink(), ifBlock);
        Assertions.assertEquals(ifBlock.getLink(), elseIfBlock);
        Assertions.assertEquals(elseIfBlock.getLink(), elseIfBodyBlock);
        Assertions.assertEquals(elseIfBodyBlock.getLink(), elseBlock);
        System.out.println(elseBlock.getLink().getKind());
        Assertions.assertEquals(elseBlock.getLink(), joinBlock);
        Assertions.assertNull(joinBlock.getLink());


        // TESTING PHI Existence
        Assertions.assertTrue(joinBlock.getInstructions().stream().anyMatch(i -> i.getOpCode().equals(Code.OpCode.phi)));

    }

    @Test
    void ensure_that_spilt_and_fix_works_correctly_with_while_loop_given_the_example_from_the_slides() {
        var parser = constructParserForTestCase("""
                                      fn main() {
                                        var a: int;
                                        var b: int;
                                        a = 10;
                                        b = 13;
                                        while (a > 0) { a = a - 1 ; }
                                      }
                """);
        parser.Parse();

        var cfg = parser.cfg;
        // System.out.println(cfg.toGraphViz());

        Assertions.assertTrue(cfg.getInstructions().size() > 1);
        Assertions.assertNotNull(cfg.getLeft());
        Assertions.assertNotNull(cfg.getLeft().getRight());
        var whileCondition = cfg.getLeft();
        var whileBody = whileCondition.getLeft();
        var joinBlock = whileCondition.getRight();


        // TESTING correct CFG order
        Assertions.assertSame(Block.BlockKind.NORMAL, cfg.getKind());
        Assertions.assertEquals(cfg.getLeft(), whileCondition);
        Assertions.assertEquals(whileCondition.getLeft(), whileBody);
        Assertions.assertEquals(whileCondition.getRight(), joinBlock);
        Assertions.assertEquals(whileBody.getRight(), whileCondition);



        // TESTING LINK ORDER
        Assertions.assertEquals(cfg.getLink(), whileCondition);
        Assertions.assertEquals(whileCondition.getLink(), whileBody);
        Assertions.assertEquals(whileBody.getLink(), joinBlock);
        Assertions.assertNull(joinBlock.getLink());
        parser.invertDomTree();
        System.out.println(cfg.dominatorTreeToGraphViz());

        // TESTING DOMINATOR TREE
        Assertions.assertEquals(cfg.getDomChildren(), List.of(whileCondition));
        Assertions.assertTrue(whileCondition.getDomChildren().containsAll(List.of(whileBody, joinBlock)));
        Assertions.assertTrue(whileBody.getDomChildren().isEmpty());
        Assertions.assertTrue(joinBlock.getDomChildren().isEmpty());


        // TESTING PHI CORRECTNESS
        Assertions.assertEquals(1, whileCondition.getInstructions().stream().filter(i -> i.getOpCode().equals(Code.OpCode.phi)).toList().size());
    }

/*    @Test
    void checkSSAExampleFromSlides() {
        var parser = constructParserForTestCase("""
                fn main() {
                    var a: int;
                    var b: int;
                    a = 1;
                    b = a + 1;
                    a = 2;
                    b = a + 1;
                }
                """);
        parser.Parse();
        var symbol = parser.getSymbolTable();
        var instructions = parser.cfg.getInstructions();
        System.out.println(instructions);
        // first instruction

        var firstInstruction = instructions.get(1);
        Assertions.assertEquals("a", ((Obj) firstInstruction.getX()).name);
        Assertions.assertEquals(Code.OpCode.ass, firstInstruction.getOpCode());
        Assertions.assertEquals(new Constant(1), firstInstruction.getY());

        // second instruction
        var secondInstruction = instructions.get(2);
        Assertions.assertEquals(secondInstruction.getX(), firstInstruction);
        Assertions.assertEquals(Code.OpCode.plus, secondInstruction.getOpCode());
        Assertions.assertEquals(new Constant(1), secondInstruction.getY());
    }*/
}