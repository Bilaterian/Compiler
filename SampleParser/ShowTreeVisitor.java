import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent(int level) {
    for (int i = 0; i < level * SPACES; i++)
      System.out.print(" ");
  }

  public void visit(ExpList expList, int level, Boolean isAddress) {
    while (expList != null) {
      try {
        expList.head.accept(this, level, false);
        expList = expList.tail;
      } catch (Exception e) {
        expList = expList.tail;
      }
    }
  }

  public void visit(AssignExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("AssignExp:");
    level++;
    exp.lhs.accept(this, level, false);
    exp.rhs.accept(this, level, false);
  }

  public void visit(IfExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("IfExp:");
    level++;
    exp.test.accept(this, level, false);
    exp.thenpart.accept(this, level, false);

    if (exp.elsepart != null) {
      indent(level);
      System.out.println("Else:");
      level++;
      exp.elsepart.accept(this, level, false);
    }
  }

  public void visit(IntExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("IntExp: " + exp.value);
  }

  public void visit(OpExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.print("OpExp:");
    switch (exp.op) {
      case OpExp.PLUS:
        System.out.println(" + ");
        break;
      case OpExp.MINUS:
        System.out.println(" - ");
        break;
      case OpExp.MUL:
        System.out.println(" * ");
        break;
      case OpExp.DIV:
        System.out.println(" / ");
        break;
      case OpExp.EQ:
        System.out.println(" == ");
        break;
      case OpExp.NE:
        System.out.println(" != ");
        break;
      case OpExp.LT:
        System.out.println(" < ");
        break;
      case OpExp.LE:
        System.out.println(" <= ");
        break;
      case OpExp.GT:
        System.out.println(" > ");
        break;
      case OpExp.GE:
        System.out.println(" >= ");
        break;
      default:
        System.out.println("Unrecognized operator at line " + exp.row + " and column " + exp.col);
    }
    level++;
    exp.left.accept(this, level, false);
    exp.right.accept(this, level, false);
  }

  public void visit(VarExp exp, int level, Boolean isAddress) {
    // indent(level);
    // System.out.println("VarExp: ");
    // level++;
    exp.variable.accept(this, level, false);
  }

  public void visit(CallExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("CallExp: " + exp.func);
    level++;

    ExpList args = exp.args;
    while (args != null) {
      try {
        args.head.accept(this, level, false);
        args = args.tail;
      } catch (Exception e) {
        args = args.tail;
      }
    }

  }

  public void visit(CompoundExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("CompoundExp: ");
    level++;
    VarDecList decs = exp.decs;
    while (decs != null) {
      try {
        decs.head.accept(this, level, false);
        decs = decs.tail;
      } catch (Exception e) {
        decs = decs.tail;
      }
    }
    ExpList exps = exp.exps;
    while (exps != null) {
      try {
        exps.head.accept(this, level, false);
        exps = exps.tail;
      } catch (Exception e) {
        exps = exps.tail;
      }
    }

  }

  public void visit(VarDecList varDecList, int level, Boolean isAddress) {
    while (varDecList != null) {
      try {
        varDecList.head.accept(this, level, false);
        varDecList = varDecList.tail;
      } catch (Exception e) {
        varDecList = varDecList.tail;
      }
    }
  }

  public void visit(DecList decList, int level, Boolean isAddress) {
    while (decList != null) {
      try {
        decList.head.accept(this, level, false);
        decList = decList.tail;
      } catch (Exception e) {
        decList = decList.tail;
      }
    }
  }

  public void visit(FunctionDec functionDec, int level, Boolean isAddress) {
    indent(level);
    System.out.println("FunctionDec: " + functionDec.func);
    visit(functionDec.result, level, false);
    level++;
    VarDecList params = functionDec.params;
    while (params != null) {
      try {
        params.head.accept(this, level, false);
        params = params.tail;
      } catch (Exception e) {
        params = params.tail;
      }
    }
    functionDec.body.accept(this, level, false);
  }

  public void visit(IndexVar indexVar, int level, Boolean isAddress) {
    indent(level);
    System.out.println("IndexVar: " + indexVar.name);
    level++;
    indexVar.index.accept(this, level, false);
  }

  public void visit(NameTy nameTy, int level, Boolean isAddress) {
    indent(level);
    System.out.print("NameTy:");
    switch (nameTy.typ) {
      case NameTy.INT:
        System.out.println(" INT ");
        break;
      case NameTy.VOID:
        System.out.println(" VOID ");
        break;
      default:
        System.out.println("Unrecognized name type at line " + nameTy.row + " and column " + nameTy.col);
    }
  }

  public void visit(NilExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("NilExp");
  }

  public void visit(ReturnExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("ReturnExp: ");
    level++;
    if (exp.exp != null)
      exp.exp.accept(this, level, false);
  }

  public void visit(SimpleDec dec, int level, Boolean isAddress) {
    indent(level);
    System.out.println("SimpleDec: " + dec.name);

  }

  public void visit(SimpleVar var, int level, Boolean isAddress) {
    indent(level);
    System.out.println("SimpleVar: " + var.name);
  }

  public void visit(WhileExp exp, int level, Boolean isAddress) {
    indent(level);
    System.out.println("WhileExp");
    level++;
    exp.test.accept(this, level, false);
    if (exp.body != null)
      exp.body.accept(this, level, false);
  }

  public void visit(ArrayDec dec, int level, Boolean isAddress) {
    indent(level);
    if (dec.type.typ == 0) {
      System.out.println("ArrayDec: " + "int " + dec.name + " [" + dec.size.value + "]");
    } else if (dec.type.typ == 1) {
      System.out.println("ArrayDec: " + "void " + dec.name + " [" + dec.size.value + "]");
    }

  }
}
