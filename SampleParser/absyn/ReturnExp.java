package absyn;

public class ReturnExp extends Exp {
	public Exp exp;

	public ReturnExp(int row, int col, Exp exp) {
		this.row = row;
		this.col = col;
		this.exp = exp;
	}

	// public void accept(AbsynVisitor visitor, int level) {
	// 	visitor.visit(this, level);
	// }

	public void accept(AbsynVisitor visitor, int offset, Boolean isAddress) {
		visitor.visit(this, offset, isAddress);
	}
}