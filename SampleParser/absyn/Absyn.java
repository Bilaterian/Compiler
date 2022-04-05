package absyn;

abstract public class Absyn {
  public int row;
  public int col;

  abstract public void accept(AbsynVisitor visitor, int level);

  abstract public void accept(AbsynVisitor visitor, int offset, Boolean isAddress);

}
