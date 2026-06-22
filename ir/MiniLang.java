package ir;

public class MiniLang {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java MiniLang <filename>");
            return;
        }
        String filename = args[0];
        Parser p = new Parser(new Scanner(filename));
        p.Parse();
        p.Optimize();
        System.out.println(p.cfg.toGraphViz());
    }
}
