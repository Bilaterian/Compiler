package absyn;

public class NameTy extends Absyn{
	final static int INT  = 0;
	final static int VOID = 1;
	
	public int typ;
	
	public NameTy(int row, int col, int typ){
		this.row = row;
		this.col = col;
		this.typ = typ;
	}
	
	public void accept( AbsynVisitor visitor, int level ) {
		visitor.visit( this, level );
	}
}