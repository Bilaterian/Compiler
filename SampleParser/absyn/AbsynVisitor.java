package absyn;

public interface AbsynVisitor {

  public void visit(ExpList exp, int level);

  public void visit(AssignExp exp, int level);

  public void visit(IfExp exp, int level);

  public void visit(IntExp exp, int level);

  public void visit(OpExp exp, int level);

  public void visit(VarExp exp, int level);

  public void visit(DecList exp, int level);

  public void visit(NameTy exp, int level);

  public void visit(FunctionDec functionDec, int level);

  public void visit(IndexVar indexVar, int level);

  public void visit(NilExp nilExp, int level);

  public void visit(ReturnExp returnExp, int level);

  public void visit(SimpleVar simpleVar, int level);

  public void visit(VarDecList varDecList, int level);

  public void visit(WhileExp whileExp, int level);

  public void visit(CompoundExp compoundExp, int level);

  public void visit(CallExp callExp, int level);

  public void visit(SimpleDec simpleDec, int level);

  public void visit(ArrayDec arrayDec, int level);

}
