package absyn;

public class NilExp extends Exp {
	public NilExp(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}

	public void accept(AbsynVisitor visitor, int offset, Boolean isAddress) {
		visitor.visit(this, offset, isAddress);
	}
}