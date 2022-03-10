import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent(int level) {
    for (int i = 0; i < level * SPACES; i++)
      System.out.print(" ");
  }

  public void visit(ExpList expList, int level) {
    while (expList != null) {
	  try{
		  expList.head.accept(this, level);
		  expList = expList.tail;
	  }
      catch(Exception e){
		  expList = expList.tail;
	  }
    }
  }

  public void visit(AssignExp exp, int level) {
    indent(level);
    System.out.println("AssignExp:");
    level++;
    exp.lhs.accept(this, level);
    exp.rhs.accept(this, level);
  }

  public void visit(IfExp exp, int level) {
    indent(level);
    System.out.println("IfExp:");
    level++;
    exp.test.accept(this, level);
    exp.thenpart.accept(this, level);

    if (exp.elsepart != null) {
      indent(level);
      System.out.println("Else:");
      level++;
      exp.elsepart.accept(this, level);
    }
  }

  public void visit(IntExp exp, int level) {
    indent(level);
    System.out.println("IntExp: " + exp.value);
  }

  public void visit(OpExp exp, int level) {
    indent(level);
    System.out.print("OpExp:");
    switch (exp.op) {
      case OpExp.PLUS:
        System.out.println(" + ");
        break;
      case OpExp.MINUS:
        System.out.println(" - ");
        break;
      case OpExp.MUL:
        System.out.println(" * ");
        break;
      case OpExp.DIV:
        System.out.println(" / ");
        break;
      case OpExp.EQ:
        System.out.println(" == ");
        break;
      case OpExp.NE:
        System.out.println(" != ");
        break;
      case OpExp.LT:
        System.out.println(" < ");
        break;
      case OpExp.LE:
        System.out.println(" <= ");
        break;
      case OpExp.GT:
        System.out.println(" > ");
        break;
      case OpExp.GE:
        System.out.println(" >= ");
        break;
      default:
        System.out.println("Unrecognized operator at line " + exp.row + " and column " + exp.col);
    }
    level++;
    exp.left.accept(this, level);
    exp.right.accept(this, level);
  }

  public void visit(VarExp exp, int level) {
    // indent(level);
    // System.out.println("VarExp: ");
    // level++;
    exp.variable.accept(this, level);
  }

  public void visit(CallExp exp, int level) {
    indent(level);
    System.out.println("CallExp: " + exp.func);
    level++;

    ExpList args = exp.args;
    while (args != null) {
      try{
		args.head.accept(this, level);
		args = args.tail;
	  }
	  catch(Exception e){
		args = args.tail;
	  }
    }

  }

  public void visit(CompoundExp exp, int level) {
    indent(level);
    System.out.println("CompoundExp: ");
    level++;
    VarDecList decs = exp.decs;
    while (decs != null) {
      try{
		decs.head.accept(this, level);
		decs = decs.tail;
	  }
	  catch(Exception e){
		decs = decs.tail; 
	  }
    }
    ExpList exps = exp.exps;
    while (exps != null) {
      try{
		exps.head.accept(this, level);
		exps = exps.tail;
	  }
	  catch(Exception e){
		exps = exps.tail;
	  }
    }

  }

  public void visit(VarDecList varDecList, int level) {
    while (varDecList != null) {
      try{
		varDecList.head.accept(this, level);
		varDecList = varDecList.tail;
	  }
	  catch(Exception e){
		varDecList = varDecList.tail;
	  }
    }
  }

  public void visit(DecList decList, int level) {
    while (decList != null) {
      try{
		decList.head.accept(this, level);
		decList = decList.tail;
	  }
	  catch(Exception e){
		decList = decList.tail;
	  }
    }
  }

  public void visit(FunctionDec functionDec, int level) {
    indent(level);
    System.out.println("FunctionDec: " + functionDec.func);
    visit(functionDec.result, level);
    level++;
    VarDecList params = functionDec.params;
    while (params != null) {
      try{
		params.head.accept(this, level);
		params = params.tail;
	  }
	  catch(Exception e){
		params = params.tail;
	  }
    }
    functionDec.body.accept(this, level);
  }

  public void visit(IndexVar indexVar, int level) {
    indent(level);
    System.out.println("IndexVar: " + indexVar.name);
    level++;
    indexVar.index.accept(this, level);
  }

  public void visit(NameTy nameTy, int level) {
    indent(level);
    System.out.print("NameTy:");
    switch (nameTy.typ) {
      case NameTy.INT:
        System.out.println(" INT ");
        break;
      case NameTy.VOID:
        System.out.println(" VOID ");
        break;
      default:
        System.out.println("Unrecognized name type at line " + nameTy.row + " and column " + nameTy.col);
    }
  }

  public void visit(NilExp exp, int level) {
    indent(level);
    System.out.println("NilExp");
  }

  public void visit(ReturnExp exp, int level) {
    indent(level);
    System.out.println("ReturnExp: ");
    level++;
    if (exp.exp != null)
      exp.exp.accept(this, level);
  }

  public void visit(SimpleDec dec, int level) {
    indent(level);
    System.out.println("SimpleDec: " + dec.name);

  }

  public void visit(SimpleVar var, int level) {
    indent(level);
    System.out.println("SimpleVar: " + var.name);
  }

  public void visit(WhileExp exp, int level) {
    indent(level);
    System.out.println("WhileExp");
    level++;
    exp.test.accept(this, level);
    if (exp.body != null)
      exp.body.accept(this, level);
  }

  public void visit(ArrayDec dec, int level) {
    indent(level);
    System.out.println("ArrayDec: " + dec.name);

  }
}
