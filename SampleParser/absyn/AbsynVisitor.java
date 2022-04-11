package absyn;

public interface AbsynVisitor {

  public void visit(ExpList expList, int offset, Boolean isAddress);

  public void visit(AssignExp exp, int offset, Boolean isAddress);

  public void visit(IfExp exp, int offset, Boolean isAddress);

  public void visit(IntExp exp, int offset, Boolean isAddress);

  public void visit(OpExp exp, int offset, Boolean isAddress);

  public void visit(VarExp exp, int offset, Boolean isAddress);

  public void visit(DecList decList, int offset, Boolean isAddress);

  public void visit(NameTy nameTy, int offset, Boolean isAddress);

  public void visit(FunctionDec functionDec, int offset, Boolean isAddress);

  public void visit(IndexVar indexVar, int offset, Boolean isAddress);

  public void visit(NilExp exp, int offset, Boolean isAddress);

  public void visit(ReturnExp exp, int offset, Boolean isAddress);

  public void visit(SimpleVar var, int offset, Boolean isAddress);

  public void visit(VarDecList varDecList, int offset, Boolean isAddress);

  public void visit(WhileExp exp, int offset, Boolean isAddress);

  public void visit(CompoundExp exp, int offset, Boolean isAddress);

  public void visit(CallExp exp, int offset, Boolean isAddress);

  public void visit(SimpleDec dec, int offset, Boolean isAddress);

  public void visit(ArrayDec dec, int offset, Boolean isAddress);

}
