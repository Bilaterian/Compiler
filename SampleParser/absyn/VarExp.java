package absyn;

public class VarExp extends Exp {
  public Var variable;

  public VarExp(int row, int col, Var variable) {
    this.row = row;
    this.col = col;
    this.variable = variable;
    this.dtype = null;
  }

  // public void accept(AbsynVisitor visitor, int level) {
  // visitor.visit(this, level);
  // }

  public void accept(AbsynVisitor visitor, int offset, Boolean isAddress) {
    visitor.visit(this, offset, isAddress);
  }
}
