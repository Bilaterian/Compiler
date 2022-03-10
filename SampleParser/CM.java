import java.io.*;
import absyn.*;

class CM {
  public final static boolean SHOW_TREE = true;
  private static boolean hasA = false;

  static public void main(String argv[]) {
    /* Start the parser */
	for(String arg : argv){
		if(arg.equals("-a")){
			hasA = true;
		}
	}
	
    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Absyn result = (Absyn) (p.parse().value);
      if (hasA && result != null) {
        System.out.println("The abstract syntax tree is:");
        ShowTreeVisitor visitor = new ShowTreeVisitor();
        result.accept(visitor, 0);
      }
    } catch (Exception e) {
      /* do cleanup here -- possibly rethrow e */
      e.printStackTrace();
    }
  }
}
