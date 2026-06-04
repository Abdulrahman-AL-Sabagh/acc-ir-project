package ir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ParserTest {

    @Test
    void checkIfTheCodeOnSlide29ChapterIntermediateRepresentationPart1CanBeGeneratedCorrectly() {

        var parser = new Parser(
                new Scanner(new ByteArrayInputStream(
                        """
                        fn main() {
                        var a: int;
                        var b: int;
                        var c: int;
                        c = 2;
                        b = 1;
                        a = 3 + b * c;
                        
                        }
                                """.getBytes())
                ));
        parser.Parse();
        System.out.println(parser.cfg);
        Assertions.assertFalse(parser.cfg.instructions().isEmpty());
        Assertions.assertEquals(5, parser.cfg.instructions().size());
    }
}