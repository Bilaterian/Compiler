package absyn;

public class CallExp extends Exp {
	public String func;
	public ExpList args;

	public CallExp(int row, int col, String func, ExpList args) {
		this.row = row;
		this.col = col;
		this.func = func;
		this.args = args;
		this.dtype = null;
	}

	// public void accept(AbsynVisitor visitor, int level) {
	// visitor.visit(this, level);
	// }

	public void accept(AbsynVisitor visitor, int offset, Boolean isAddress) {
		visitor.visit(this, offset, isAddress);
	}
}