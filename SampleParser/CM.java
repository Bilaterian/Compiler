import java.io.*;
import absyn.*;

class CM {
  public final static boolean SHOW_TREE = true;
  private static boolean hasA = false;
  private static boolean hasS = false;
  private static boolean hasC = false;
  private static String file;
  private static String filename;

  static public void main(String argv[]) {
    /* Start the parser */
    for (String arg : argv) {
      if (arg.equals("-a")) {
        hasA = true;
      }
    }
    for (String arg : argv) {
      if (arg.equals("-s")) {
        hasS = true;
      }
    }

    for (String arg : argv) {
      if (arg.equals("-c")) {
        hasC = true;
      } else if (arg.length() > 3 && arg.substring(arg.length() - 3).equals(".cm")) {
        file = arg;
      }
    }

    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      PrintStream console = System.out;

      Absyn result = (Absyn) (p.parse().value);
      if (hasS && result != null) {
        System.out.println("The abstract syntax tree is:");
        ShowTreeVisitor visitor = new ShowTreeVisitor();
        result.accept(visitor, 0, false);
        SemanticAnalyzer semantics = new SemanticAnalyzer();
        result.accept(semantics, 0, false);
      } else if (hasA && result != null) {
        System.out.println("The abstract syntax tree is:");
        ShowTreeVisitor visitor = new ShowTreeVisitor();
        result.accept(visitor, 0, false);
      } else if (hasC && result != null) {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        System.out.println("The abstract syntax tree is:");
        ShowTreeVisitor visitor = new ShowTreeVisitor();
        result.accept(visitor, 0, false);
        SemanticAnalyzer semantics = new SemanticAnalyzer();
        result.accept(semantics, 0, false);
        System.setOut(console);

        CodeGenerator cg = new CodeGenerator();
        result.accept(cg, 0, false);
        filename = file.substring(file.lastIndexOf('/') + 1, file.lastIndexOf('.'));
        File tmFile = new File(filename + ".tm");
        FileOutputStream fp = new FileOutputStream(tmFile);
        PrintStream ps = new PrintStream(fp);
        System.setOut(ps);
        cg.visit(result, filename);
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}
