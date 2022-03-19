import java.util.ArrayList;
import java.util.HashMap;

import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {
    HashMap<String, ArrayList<NodeType>> symbolTable;

    public SemanticAnalyzer() {
        symbolTable = new HashMap<String, ArrayList<NodeType>>();
    }

    final static int SPACES = 4;

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++)
            System.out.print(" ");
    }

    public void visit(ExpList expList, int level) {
        while (expList != null) {
            try {
                expList.head.accept(this, level);
                expList = expList.tail;
            } catch (Exception e) {
                expList = expList.tail;
            }
        }
    }

    public void visit(AssignExp exp, int level) {
        //indent(level);
        //System.out.println("AssignExp:");
        //level++;
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
    }

    public void visit(IfExp exp, int level) {
        //indent(level);
        //System.out.println("IfExp:");
        level++;
        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);

        if (exp.elsepart != null) {
            //indent(level);
            //System.out.println("Else:");
            level++;
            exp.elsepart.accept(this, level);
        }
    }

    public void visit(IntExp exp, int level) {
        //indent(level);
        //System.out.println("IntExp: " + exp.value);
    }

    public void visit(OpExp exp, int level) {
        //indent(level);
        //System.out.print("OpExp:");
        switch (exp.op) {
            case OpExp.PLUS:
                //System.out.println(" + ");
                break;
            case OpExp.MINUS:
                //System.out.println(" - ");
                break;
            case OpExp.MUL:
                //System.out.println(" * ");
                break;
            case OpExp.DIV:
                //System.out.println(" / ");
                break;
            case OpExp.EQ:
                //System.out.println(" == ");
                break;
            case OpExp.NE:
                //System.out.println(" != ");
                break;
            case OpExp.LT:
                //System.out.println(" < ");
                break;
            case OpExp.LE:
                //System.out.println(" <= ");
                break;
            case OpExp.GT:
                //System.out.println(" > ");
                break;
            case OpExp.GE:
                //System.out.println(" >= ");
                break;
            default:
                //System.out.println("Unrecognized operator at line " + exp.row + " and column " + exp.col);
        }
        //level++;
        exp.left.accept(this, level);
        exp.right.accept(this, level);
    }

    public void visit(VarExp exp, int level) {
        //indent(level);
        //System.out.println("VarExp: ");
        //level++;
        exp.variable.accept(this, level);
    }

    public void visit(CallExp exp, int level) {
        //indent(level);
        //System.out.println("CallExp: " + exp.func);
        //level++;

        ExpList args = exp.args;
        while (args != null) {
            try {
                args.head.accept(this, level);
                args = args.tail;
            } catch (Exception e) {
                args = args.tail;
            }
        }
    }

    public void visit(CompoundExp exp, int level) {
        //indent(level);
        //System.out.println("CompoundExp: ");
        level++;
        VarDecList decs = exp.decs;
        while (decs != null) {
            try {
                decs.head.accept(this, level);
                decs = decs.tail;
            } catch (Exception e) {
                decs = decs.tail;
            }
        }
        ExpList exps = exp.exps;
        while (exps != null) {
            try {
                exps.head.accept(this, level);
                exps = exps.tail;
            } catch (Exception e) {
                exps = exps.tail;
            }
        }

    }

    public void visit(VarDecList varDecList, int level) {
        while (varDecList != null) {
            try {
                varDecList.head.accept(this, level);
                varDecList = varDecList.tail;
            } catch (Exception e) {
                varDecList = varDecList.tail;
            }
        }
    }

    public void visit(DecList decList, int level) {
        while (decList != null) {
            try {
                decList.head.accept(this, level);
                decList = decList.tail;
            } catch (Exception e) {
                decList = decList.tail;
            }
        }
    }

    public void visit(FunctionDec functionDec, int level) {
        //System.out.println("FunctionDec: " + functionDec.func);
		NodeType node = new NodeType(functionDec.func, functionDec, level);
		
		if(hasLevel(level + 1) == true){
			indent(level);
			System.out.println("Leaving the block");
			removeLevel(level + 1);
		}
		if(hasLevel(level) == false){
			indent(level);
			System.out.println("Entering a new block:");
		}
			
		insertNodeToSymbolTable(node);
		indent(level);
		System.out.println(functionDec.func + ": ("+ getStringFromParams(functionDec.params) + ") -> " + printType(functionDec.result.typ));
		
        visit(functionDec.result, level);
        level++;
        VarDecList params = functionDec.params;
        while (params != null) {
            try {
                params.head.accept(this, level);
                params = params.tail;
            } catch (Exception e) {
                params = params.tail;
            }
        }
        functionDec.body.accept(this, level);
    }

    public void visit(IndexVar indexVar, int level) {
        //indent(level);
        //System.out.println("IndexVar: " + indexVar.name);
        //level++;
        indexVar.index.accept(this, level);
    }

    public void visit(NameTy nameTy, int level) {
        //indent(level);
        //System.out.print("NameTy:");
        /*switch (nameTy.typ) {
            case NameTy.INT:
                //System.out.println(" INT ");
                break;
            case NameTy.VOID:
                //System.out.println(" VOID ");
                break;
            default:
                //System.out.println("Unrecognized name type at line " + nameTy.row + " and column " + nameTy.col);
        }*/
    }

    public void visit(NilExp exp, int level) {
        //indent(level);
        //System.out.println("NilExp");
    }

    public void visit(ReturnExp exp, int level) {
        //indent(level);
        //System.out.println("ReturnExp: ");
        //level++;
        if (exp.exp != null)
            exp.exp.accept(this, level);
    }

    public void visit(SimpleDec dec, int level) {
		NodeType node = new NodeType(dec.name, dec, level);
		if(hasLevel(level + 1) == true){
			indent(level);
			System.out.println("Leaving the block");
			removeLevel(level + 1);
		}
		if(hasLevel(level) == false){
			indent(level);
			System.out.println("Entering a new block:");
		}
		
		insertNodeToSymbolTable(node);
		indent(level);
		System.out.println(dec.name + ": " + printType(dec.typ.typ));
        //System.out.println("SimpleDec: " + dec.name);

    }

    public void visit(SimpleVar var, int level) {
        //indent(level);
        //System.out.println("SimpleVar: " + var.name);
    }

    public void visit(WhileExp exp, int level) {
        //indent(level);
        //System.out.println("WhileExp");
        level++;
        exp.test.accept(this, level);
        if (exp.body != null)
            exp.body.accept(this, level);
    }

    public void visit(ArrayDec dec, int level) {
		NodeType node = new NodeType(dec.name, dec, level);
		if(hasLevel(level + 1) == true){
			indent(level);
			System.out.println("Leaving the block");
			removeLevel(level + 1);
		}
		if(hasLevel(level) == false){
			indent(level);
			System.out.println("Entering a new block:");
		}
		
		insertNodeToSymbolTable(node);
		indent(level);
		System.out.println(dec.name + ": " + printType(dec.typ.typ));
        //System.out.println("ArrayDec: " + dec.name);
    }
	
	
//*******************SYMBOL TABLE HELPER FUNCTIONS*******************//
	
	private void insertNodeToSymbolTable(NodeType node) {
        if (symbolTable.containsKey(node.name)) {
            ArrayList<NodeType> list = symbolTable.get(node.name);
            list.add(node);
            symbolTable.put(node.name, list);
        } else {
            ArrayList<NodeType> list = new ArrayList<NodeType>();
            list.add(node);
            symbolTable.put(node.name, list);
        }
    }

    private NodeType getNodeFromSymbolTable(String name) {
        if (symbolTable.containsKey(name)) {
            ArrayList<NodeType> list = symbolTable.get(name);
            return list.get(list.size() - 1);
        }
        return null;
    }
	
	private boolean hasLevel(int level){
		for(String name: symbolTable.keySet()){
			for(int i = 0; i < symbolTable.get(name).size(); i++){
				if(symbolTable.get(name).get(i).level == level){
					return true;
				}
			}
		}
		return false;
	}
	
	private void removeLevel(int level){
		for(String name: symbolTable.keySet()){
			for(int i = 0; i < symbolTable.get(name).size(); i++){
				if(symbolTable.get(name).get(i).level == level){
					symbolTable.get(name).remove(i);
				}
			}
		}
	}
	
	private String printType(int typ){
		if(typ == 0){
			return "int";
		}
		else{
			return "void";
		}
	}
	
	private String getStringFromParams(VarDecList params){
		String paramString = "";
		if(params == null){
			paramString = " ";
		}
		else{
			while (params != null) {
				try {
					paramString += printType(params.head.typ.typ);
					paramString += ",";
					params = params.tail;
				} 
				catch (Exception e) {
					params = params.tail;
				}
			}
			paramString = paramString.substring(0, paramString.length() -1);
		}
		return paramString;
	}
	
}
