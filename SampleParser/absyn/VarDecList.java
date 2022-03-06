package absyn;

public class VarDecList extends Absyn {
    public Exp head;
    public VarDecList tail;

    public VarDecList(Exp head, VarDecList tail) {
        this.head = head;
        this.tail = tail;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
