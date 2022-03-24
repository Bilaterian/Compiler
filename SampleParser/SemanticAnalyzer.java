import java.util.ArrayList;
import java.util.HashMap;

import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {
	HashMap<String, ArrayList<NodeType>> symbolTable;

	public SemanticAnalyzer() {
		symbolTable = new HashMap<String, ArrayList<NodeType>>();

	}

	final static int SPACES = 4;
	int hasR = 1;

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

		if (exp.rhs.dtype instanceof ArrayDec) {
			ArrayDec temp = (ArrayDec) exp.rhs.dtype;
			exp.dtype = new ArrayDec(exp.row, exp.col, temp.typ, temp.name, temp.size);
		} else if (exp.rhs.dtype instanceof SimpleDec) {
			SimpleDec temp = (SimpleDec) exp.rhs.dtype;
			exp.dtype = new SimpleDec(exp.row, exp.col, temp.typ, temp.name);
		} else if (exp.rhs.dtype instanceof FunctionDec) {
			FunctionDec temp = (FunctionDec) exp.rhs.dtype;
			exp.dtype = new FunctionDec(exp.row, exp.col, temp.result, temp.func, temp.params, temp.body);
		}

		// DO CHECK HERE
		if (getType(exp.lhs.dtype) != getType(exp.rhs.dtype)) {
			System.err.println("Invalid assignment expression at line " + exp.row + " and column " + exp.col);
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
		NameTy type = new NameTy(exp.row, exp.col, 0);
		exp.dtype = new SimpleDec(exp.row, exp.col, type, exp.value);

		try {
			int tester = Integer.parseInt(exp.value);
		} catch (Exception e) {
			System.err.println("Invalid integer expression at line " + exp.row + " and column " + exp.col);
		}

	}

	public void visit(OpExp exp, int level) {

		exp.left.accept(this, level);
		exp.right.accept(this, level);

		NameTy type = new NameTy(exp.row, exp.col, 0);
		exp.dtype = new SimpleDec(exp.row, exp.col, type, "null");

		// DO CHECK HERE
		if (!((getType(exp.left.dtype) == 0) && (getType(exp.right.dtype) == 0))) {
			System.err.println("Invalid operation expression at line " + exp.row + " and column " + exp.col);
			System.err.println("left: " + getType(exp.left.dtype) + " right: " + getType(exp.right.dtype));
			System.err.println("left: " + exp.left.dtype + " right: " + exp.right.dtype);
		}
	}

	public void visit(VarExp exp, int level) {
		exp.variable.accept(this, level);

		// DO CHECK HERE
		NameTy type;

		if (exp.variable instanceof SimpleVar) {
			SimpleVar sv = (SimpleVar) exp.variable;
			for (String name : symbolTable.keySet()) {
				for (int i = 0; i < symbolTable.get(name).size(); i++) {
					if (symbolTable.get(name).get(i).name.equals(sv.name)) {
						if (getType(symbolTable.get(name).get(i).def) == 1) {
							type = new NameTy(exp.row, exp.col, 0);
						} else {
							type = new NameTy(exp.row, exp.col, 0);
						}
						exp.dtype = new SimpleDec(exp.row, exp.col, type, sv.name);
						break;
					}
				}
			}
		} else if (exp.variable instanceof IndexVar) {
			IndexVar iv = (IndexVar) exp.variable;
			for (String name : symbolTable.keySet()) {
				for (int i = 0; i < symbolTable.get(name).size(); i++) {
					if (symbolTable.get(name).get(i).name.equals(iv.name)) {
						if (getType(symbolTable.get(name).get(i).def) == 1) {
							type = new NameTy(exp.row, exp.col, 0);
						} else {
							type = new NameTy(exp.row, exp.col, 0);
						}
						exp.dtype = new SimpleDec(exp.row, exp.col, type, iv.name);
						break;
					}
				}
			}
		}
	}

	public void visit(CallExp exp, int level) {
		ArrayList<Integer> checkArgs = new ArrayList<Integer>();

		ExpList args = exp.args;
		while (args != null) {
			try {
				args.head.accept(this, level);
				checkArgs.add(new Integer(getType(args.head.dtype)));
				args = args.tail;
			} catch (Exception e) {
				args = args.tail;
			}
		}

		// DO CHECK HERE
		if (!ifFunctionExists(exp.func, checkArgs)) {
			System.err.println("Invalid function call at line " + exp.row + " and column " + exp.col);
		}

		// get temp
		NameTy type = new NameTy(exp.row, exp.col, 0);
		if (exp.func.equals("input") || exp.func.equals("output")) {// prebuilt funcitons that we don't control
			exp.dtype = new FunctionDec(exp.row, exp.col, type, exp.func, null, null);
		} else {
			NodeType function = getFunc(exp.func, checkArgs);
			exp.dtype = (FunctionDec) function.def;
		}
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
		if (level == 0) {
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
		if (level == 0) {
			System.out.println("Leaving the global scope");
		}
	}

	public void visit(FunctionDec functionDec, int level) {
		NodeType node = new NodeType(functionDec.func, functionDec, level);
		if (containsDec(functionDec.func)) {
			System.err.println("ERROR [row=" + functionDec.row + ", col=" + functionDec.col + "]: "
					+ functionDec.func + " is already defined ");
		}
		insertNodeToSymbolTable(node);
		indent(level + 1);
		System.out.println(functionDec.func + ": (" + getStringFromParams(functionDec.params) + ") -> "
				+ printType(functionDec.result.typ));
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
		if (functionDec.result.typ != hasR) {
			System.err.println("ERROR [row=" + functionDec.row + ", col=" + functionDec.col + "]: "
					+ functionDec.func + " has wrong return type ");
		}
		hasR = 1;
		indent(level);
		System.out.println("Leaving the function scope");
		removeLevel(level);
	}

	public void visit(IndexVar indexVar, int level) {
		indexVar.index.accept(this, level);
	}

	public void visit(NameTy nameTy, int level) {

	}

	public void visit(NilExp exp, int level) {
		NameTy type = new NameTy(exp.row, exp.col, 1);
		exp.dtype = new SimpleDec(exp.row, exp.col, type, "null");
	}

	public void visit(ReturnExp exp, int level) {
		if (exp.exp != null) {
			exp.exp.accept(this, level);
			// DO CHECK HERE
			// look for the latest function call
			exp.dtype = exp.exp.dtype;
			hasR = 0;
			if (matchFunctionToType(level - 1, getType(exp.dtype)) == true) {
				System.err.println("Invalid return expression at line " + exp.row + " and column " + exp.col);
			} else if (getType(exp.dtype) == 1) {
				System.err.println("Unexpected return function found at line " + exp.row + " and column " + exp.col);
			}
		}
	}

	public void visit(SimpleDec dec, int level) {
		NodeType node = new NodeType(dec.name, dec, level);
		if (containsDec(dec.name)) {
			System.err.println("ERROR [row=" + dec.row + ", col=" + dec.col + "]: "
					+ dec.name + " is already defined ");
		}
		insertNodeToSymbolTable(node);
		indent(level + 1);
		System.out.println(dec.name + ": " + printType(dec.typ.typ));
		// System.out.println("SimpleDec: " + dec.name);

	}

	public boolean containsDec(String name) {
		if (symbolTable.get(name) != null && symbolTable.get(name).isEmpty() != true) {
			return true;
		}
		return false;
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
		// System.out.println("ArrayDec: " + dec.name);
	}

	// *******************SYMBOL TABLE HELPER FUNCTIONS*******************//

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

	private boolean hasLevel(int level) {
		for (String name : symbolTable.keySet()) {
			for (int i = 0; i < symbolTable.get(name).size(); i++) {
				if (symbolTable.get(name).get(i).level == level) {
					return true;
				}
			}
		}
		return false;
	}

	private void removeLevel(int level) {
		for (String name : symbolTable.keySet()) {
			for (int i = 0; i < symbolTable.get(name).size(); i++) {
				if (symbolTable.get(name).get(i).level == level) {
					symbolTable.get(name).remove(i);
				}
			}
		}
	}

	private String printType(int typ) {
		if (typ == 0) {
			return "int";
		} else {
			return "void";
		}
	}

	private String getStringFromParams(VarDecList params) {
		String paramString = "";
		if (params == null) {
			paramString = " ";
		} else {
			while (params != null) {
				try {
					paramString += printType(params.head.typ.typ);
					paramString += ",";
					params = params.tail;
				} catch (Exception e) {
					params = params.tail;
				}
			}
			paramString = paramString.substring(0, paramString.length() - 1);
		}
		return paramString;
	}

	private int getType(Dec dtype) {
		if (dtype instanceof SimpleDec) {
			SimpleDec sd = (SimpleDec) dtype;
			if (sd.typ.typ == 0) {
				return NameTy.INT;
			} else {
				return NameTy.VOID;
			}
		} else if (dtype instanceof ArrayDec) {
			ArrayDec sd = (ArrayDec) dtype;
			if (sd.typ.typ == 0) {
				return NameTy.INT;
			} else {
				return NameTy.VOID;
			}
		} else if (dtype instanceof FunctionDec) {
			FunctionDec fd = (FunctionDec) dtype;
			if (fd.result.typ == 0) {
				return NameTy.INT;
			} else {
				return NameTy.VOID;
			}
		}
		return NameTy.VOID;
	}

	private boolean ifFunctionExists(String func, ArrayList<Integer> checkArgs) {
		if (func.equals("input") || func.equals("output")) {// prebuilt funcitons that we don't control
			return true;
		}

		for (String name : symbolTable.keySet()) {
			if (name.equals(func)) {
				for (int i = 0; i < symbolTable.get(name).size(); i++) {
					if (symbolTable.get(name).get(i).def instanceof FunctionDec) {
						FunctionDec fd = (FunctionDec) symbolTable.get(name).get(i).def;
						// length checks
						if (checkArgs.size() == 0) {
							if (fd.params == null) {// same lengths but no arguments, don't bother
								return true;
							} else {
								System.err.println("No matching function");
								return false;
							}
						}
						if (getVarDecListLength(fd.params) == checkArgs.size()) {// same lengths
							// check if list contains types in order
							ArrayList<Integer> paramList = getParamTypes(fd.params);
							for (int j = 0; j < paramList.size(); j++) {
								if (!checkArgs.get(j).equals(paramList.get(j))) {
									continue;
								}
							}
							return true;
						}
					}
				}
			}
		}
		System.out.println("no matching function");
		return false;
	}

	private int getVarDecListLength(VarDecList params) {
		int i = 0;
		if (params == null) {
			return 0;
		} else {
			while (params != null) {
				try {
					params = params.tail;
					i = i + 1;
				} catch (Exception e) {
					params = params.tail;
				}
			}
			return i;
		}
	}

	private ArrayList<Integer> getParamTypes(VarDecList params) {
		ArrayList<Integer> paramList = new ArrayList<Integer>();

		while (params != null) {
			try {
				paramList.add(new Integer(getType(params.head)));
				params = params.tail;
			} catch (Exception e) {
				params = params.tail;
			}
		}

		return paramList;
	}

	private NodeType getFunc(String funcName, ArrayList<Integer> checkArgs) { // we want to match
		for (String name : symbolTable.keySet()) {
			if (name.equals(funcName)) {
				for (int i = 0; i < symbolTable.get(name).size(); i++) {
					if (symbolTable.get(name).get(i).def instanceof FunctionDec) {
						FunctionDec fd = (FunctionDec) symbolTable.get(name).get(i).def;

						if (checkArgs.size() == 0) {
							if (fd.params == null) {// same lengths but no arguments, don't bother
								return symbolTable.get(name).get(i);
							}
						}
						if (getVarDecListLength(fd.params) == checkArgs.size()) {// same lengths
							// check if list contains types in order
							ArrayList<Integer> paramList = getParamTypes(fd.params);
							for (int j = 0; j < paramList.size(); j++) {
								if (!checkArgs.get(j).equals(paramList.get(j))) {
									continue;
								}
							}
							return symbolTable.get(name).get(i);
						}
					}
				}
			}
		}
		FunctionDec nullFunc = new FunctionDec(-1, -1, null, funcName, null, null);
		NodeType nullNode = new NodeType(funcName, nullFunc, -1);// placeholder that shouldn't be reached
		return nullNode;
	}

	private boolean matchFunctionToType(int level, int type) {
		int latest = 0;
		for (String name : symbolTable.keySet()) {
			for (int i = 0; i < symbolTable.get(name).size(); i++) {
				if (symbolTable.get(name).get(i).level == level) {
					if (symbolTable.get(name).get(i).def instanceof FunctionDec) {
						latest = symbolTable.get(name).get(i).def.row;
					}
				}
			}
		}
		for (String name : symbolTable.keySet()) {
			for (int i = 0; i < symbolTable.get(name).size(); i++) {
				if (symbolTable.get(name).get(i).level == level) {
					if (symbolTable.get(name).get(i).def instanceof FunctionDec) {
						if (latest == symbolTable.get(name).get(i).def.row) {
							// check dtypes here
							if (getType(symbolTable.get(name).get(i).def) == type) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			}
		}

		return false;
	}

}
