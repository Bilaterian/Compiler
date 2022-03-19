import java.io.*;
import absyn.*;

class CM {
  public final static boolean SHOW_TREE = true;
  private static boolean hasA = false;
  private static boolean hasS = false;

  static public void main(String argv[]) {
    /* Start the parser */
	for(String arg : argv){
		if(arg.equals("-a")){
			hasA = true;
		}
	}
	for(String arg : argv){
		if(arg.equals("-s")){
			hasS = true;
		}
	}
	
    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Absyn result = (Absyn) (p.parse().value);
      if(hasS && result != null){
		//System.out.println("The abstract syntax tree is:");
        //ShowTreeVisitor visitor = new ShowTreeVisitor();
        //result.accept(visitor, 0);
		System.out.println("Entering the global scope:");
		SemanticAnalyzer semantics = new SemanticAnalyzer();
		result.accept(semantics, 0);
	  }
	  else if (hasA && result != null) {
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
