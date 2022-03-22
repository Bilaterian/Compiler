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
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
		
		//DO CHECK HERE
		if(!(getType(exp.lhs.dtype) == getType(exp.rhs.dtype))){
			System.out.println("Invalid integer value at line " + exp.row + " and column " + exp.col);
			System.out.println("lhs: " + exp.lhs.dtype + " rhs: " + exp.rhs.dtype);
		}
    }

    public void visit(IfExp exp, int level) {
        level++;
		indent(level);
		System.out.println("Entering a new block: ");
        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);

        if (exp.elsepart != null) {
            level++;
            exp.elsepart.accept(this, level);
			removeLevel(level);
			level = level - 1;
        }
		indent(level);
		System.out.println("Leaving the block");
		removeLevel(level);
    }

    public void visit(IntExp exp, int level) {
		try{
			int tester = Integer.parseInt(exp.value);
		}
		catch(Exception e){
			System.out.println("Invalid integer value at line " + exp.row + " and column " + exp.col);
		}
    }

    public void visit(OpExp exp, int level) {
		
        exp.left.accept(this, level);
        exp.right.accept(this, level);
		
		NameTy type = new NameTy(exp.row, exp.col, 0);
		exp.dtype = new SimpleDec(exp.row, exp.col, type, "null");
		
		//DO CHECK HERE
		if(!((getType(exp.left.dtype) == 0) && (getType(exp.right.dtype) == 0))){
			System.out.println("Invalid integer value at line " + exp.row + " and column " + exp.col);
			System.out.println("left: " + exp.left.dtype + " right: " + exp.right.dtype);
		}
    }

    public void visit(VarExp exp, int level) {

        exp.variable.accept(this, level);
		
		//DO CHECK HERE
    }

    public void visit(CallExp exp, int level) {

        ExpList args = exp.args;
        while (args != null) {
            try {
                args.head.accept(this, level);
                args = args.tail;
            } catch (Exception e) {
                args = args.tail;
            }
        }
		
		//DO CHECK HERE
    }

    public void visit(CompoundExp exp, int level) {
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

		//DO CHECK HERE
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
		if(level == 0){
			System.out.println("Entering the global scope:");
		}
        while (decList != null) {
            try {
                decList.head.accept(this, level);
                decList = decList.tail;
            } catch (Exception e) {
                decList = decList.tail;
            }
        }
		if(level == 0){
			System.out.println("Leaving the global scope");
		}
    }

    public void visit(FunctionDec functionDec, int level) {
        //System.out.println("FunctionDec: " + functionDec.func);
		NodeType node = new NodeType(functionDec.func, functionDec, level);
			
		insertNodeToSymbolTable(node);
		indent(level + 1);
		System.out.println(functionDec.func + ": ("+ getStringFromParams(functionDec.params) + ") -> " + printType(functionDec.result.typ));
        visit(functionDec.result, level);
        level++;
		indent(level);
		System.out.println("Entering the scope for function " + functionDec.func + ": ");
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
		indent(level);
		System.out.println("Leaving the function scope");
		removeLevel(level);
    }

    public void visit(IndexVar indexVar, int level) {
        indexVar.index.accept(this, level);
		
		//DO CHECK HERE
    }

    public void visit(NameTy nameTy, int level) {

    }

    public void visit(NilExp exp, int level) {
		NameTy type = new NameTy(exp.row, exp.col, 1);
		exp.dtype = new SimpleDec(exp.row, exp.col, type, "null");
    }

    public void visit(ReturnExp exp, int level) {
        if (exp.exp != null)
            exp.exp.accept(this, level);
			//DO CHECK HERE
			exp.dtype = exp.exp.dtype;
			//check if function call was made in the scope previous
    }

    public void visit(SimpleDec dec, int level) {
		NodeType node = new NodeType(dec.name, dec, level);
		insertNodeToSymbolTable(node);
		indent(level + 1);
		System.out.println(dec.name + ": " + printType(dec.typ.typ));
        //System.out.println("SimpleDec: " + dec.name);

    }

    public void visit(SimpleVar var, int level) {
		
    }

    public void visit(WhileExp exp, int level) {
        level++;
		indent(level);
		System.out.println("Entering a new block: ");
        exp.test.accept(this, level);
        if (exp.body != null)
            exp.body.accept(this, level);
		indent(level);
		System.out.println("Leaving the block");
		removeLevel(level);
    }

    public void visit(ArrayDec dec, int level) {
		NodeType node = new NodeType(dec.name, dec, level);
		insertNodeToSymbolTable(node);
		indent(level + 1);
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
	
	private int getType(Dec dtype) {
        if (dtype instanceof SimpleDec) {
            SimpleDec sd = (SimpleDec) dtype;
            if (sd.typ.typ == 0) {
                return NameTy.INT;
            }
			else{
				return NameTy.VOID;
			}
        }
		else if (dtype instanceof ArrayDec) {
            ArrayDec sd = (ArrayDec) dtype;
            if (sd.typ.typ == 0) {
                return NameTy.INT;
            }
			else{
				return NameTy.VOID;
			}
        } 
		else if (dtype instanceof FunctionDec) {
            FunctionDec fd = (FunctionDec) dtype;
            if (fd.result.typ == 0) {
                return NameTy.INT;
            }
			else{
				return NameTy.VOID;
			}
        }
        return NameTy.VOID;
    }
}
