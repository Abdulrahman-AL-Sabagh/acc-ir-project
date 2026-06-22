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
        var entryBlock = parser.cfg;
        var firstBlock = entryBlock.getLeft();
        System.out.println(entryBlock);
        System.out.println(firstBlock);
        System.out.println(parser.curBlock.getInstructions());
        Assertions.assertFalse(firstBlock.getInstructions().isEmpty());
        Assertions.assertEquals(5, firstBlock.getInstructions().size());
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

        var entry = parser.cfg;
        var cfg = entry.getLeft();
        var generatedElseBlock = cfg.getRight();
        var ifBlock = cfg.getLeft();
        var joinBlock = cfg.getRight().getLeft();
        System.out.println(parser.cfg.toGraphViz());
        Assertions.assertNotNull(parser.cfg.getLeft());
        Assertions.assertEquals(Block.BlockKind.IF, cfg.getLeft().getKind());
        Assertions.assertFalse(parser.cfg.getLeft().getInstructions().isEmpty());
        Assertions.assertNotNull(parser.cfg.getRight());
        Assertions.assertSame(Block.BlockKind.NORMAL, cfg.getRight().getKind());
        Assertions.assertSame(Block.BlockKind.NORMAL, generatedElseBlock.getKind());
        Assertions.assertTrue(generatedElseBlock.getInstructions().isEmpty());
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
        Assertions.assertNotNull(joinBlock.getLink());

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

        var cfg = parser.cfg.getLeft();
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
        Assertions.assertSame(joinBlock.getLink().getKind(), Block.BlockKind.EXIT);


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

        var cfg = parser.cfg.getLeft();
        System.out.println(cfg.toGraphViz());

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
        Assertions.assertSame(joinBlock.getLink().getKind(), Block.BlockKind.EXIT);
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


    @Test
    void testing_provided_test2() {

        var parser = constructParserForTestCase("""
                                      var i: int;
                                              var sum: int;
                                              var a: int[10];
                
                                              fn main() {
                                                  i = 0;
                                                  while (i < 10) {
                                                      read a[i];
                                                      i = i + 1;
                                                  }
                
                                                  i = 1;
                                                  sum = 0;
                                                  while (i < 10) {
                                                      sum = sum + (a[i] - a [i - 1]);
                                                      write sum;
                                                      i = i + 1;
                                                  }
                                                  write sum;
                                              }
                
                """);
        parser.Parse();
        var entry = parser.cfg;
        var cfg = entry.getLeft();
        // CFG Assertions
        var firstWhile = cfg.getLeft();
        var firstWhileBody = firstWhile.getLeft();
        var firstWhileJoin = firstWhile.getRight();
        var secondWhile = firstWhileJoin.getLeft();
        var secondWhileBody = secondWhile.getLeft();
        var secondWhileJoin = secondWhile.getRight();


        Assertions.assertSame(cfg.getLeft(), firstWhile);
        Assertions.assertSame(cfg.getLeft().getLeft(), firstWhileBody);
        Assertions.assertSame(cfg.getLeft().getRight(), firstWhileJoin);

        System.out.println(firstWhile);
        // System.out.println(entry.toGraphViz());
        parser.Optimize();
        System.out.println(cfg.toGraphViz());


    }

    @Test
    void validateCodeWith_GCSEExampleFromTheSlides_Chapter7Slide27() {
        var parser = constructParserForTestCase("""
                var a: int;
                var b: int;
                var c: int;
                var d: int;
                fn main() {
                    d = a + b;
                    c = 1 + 2;
                    d = a + b;
                    d = a + b - 1;
                    d = a + b;
                    d = (a + b) * (a + b - 1);
                }
                """
        );

        parser.Parse();
        parser.Optimize();
        var cfg = parser.cfg.getLeft();

        Assertions.assertEquals(4, cfg.getInstructions().size());
        System.out.println(cfg);
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