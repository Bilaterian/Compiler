package absyn;

public class SimpleDec extends VarDec {
	public String name;

	public SimpleDec(int row, int col, NameTy typ, String name) {
		this.row = row;
		this.col = col;
		this.typ = typ;
		this.name = name;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}

	public void accept(AbsynVisitor visitor, int offset, Boolean isAddress) {
		visitor.visit(this, offset, isAddress);
	}
}