import java.util.ArrayList;
import java.util.HashMap;

import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {
    HashMap<String, ArrayList<NodeType>> symbolTable;
    int globalLevel = 0;

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

        if (exp.rhs.dtype instanceof ArrayDec) {
            ArrayDec temp = (ArrayDec) exp.rhs.dtype;
            exp.dtype = new ArrayDec(exp.row, exp.col, temp.typ, temp.name, temp.size);
            // NodeType node = new NodeType(temp.name, exp.dtype, level);
            // insertNodeToSymbolTable(node);

        } else if (exp.rhs.dtype instanceof SimpleDec) {
            SimpleDec temp = (SimpleDec) exp.rhs.dtype;
            exp.dtype = new SimpleDec(exp.row, exp.col, temp.typ, temp.name);
            // NodeType node = new NodeType(temp.name, exp.dtype, level);
            // insertNodeToSymbolTable(node);
        } else if (exp.rhs.dtype instanceof FunctionDec) {
            FunctionDec temp = (FunctionDec) exp.rhs.dtype;
            exp.dtype = new FunctionDec(exp.row, exp.col, temp.result, temp.func, temp.params, temp.body);
            // NodeType node = new NodeType(temp.func, exp.dtype, level);
            // insertNodeToSymbolTable(node);
        }
        // DO CHECK HERE
        if (!(getType(exp.lhs.dtype) == getType(exp.rhs.dtype))) {
            System.out.println("Invalid assignment expression at line " + exp.row + " and column " + exp.col);
            System.out.println("left: " + exp.lhs.dtype + " right: " + exp.rhs.dtype);
        }
    }

    public void visit(IfExp exp, int level) {
        exp.test.accept(this, level);

        level++;
        globalLevel++;
        indent(level);
        System.out.println("Entering a new block of IFEXP: ");
        exp.thenpart.accept(this, level);

        printLevel(level);
        removeLevel(level);
        indent(level);
        System.out.println("Leaving the block of IFEXP");

        if (exp.elsepart != null) {
            indent(level);
            System.out.println("Entering a new block of IFEXP ELSE: ");
            exp.elsepart.accept(this, level);
            printLevel(level);
            indent(level);
            System.out.println("Leaving the block of IFEXP ELSE: ");
            removeLevel(level);
        }

        removeLevel(level);
    }

    public void visit(IntExp exp, int level) {
        NameTy type = new NameTy(exp.row, exp.col, 0);
        exp.dtype = new SimpleDec(exp.row, exp.col, type, exp.value);

        try {
            int tester = Integer.parseInt(exp.value);
        } catch (Exception e) {
            System.out.println("Invalid integer expression at line " + exp.row + " and column " + exp.col);
        }
    }

    public void visit(OpExp exp, int level) {

        exp.left.accept(this, level);
        exp.right.accept(this, level);

        NameTy type = new NameTy(exp.row, exp.col, 0);
        exp.dtype = new SimpleDec(exp.row, exp.col, type, "null");

        // DO CHECK HERE
        if (!((getType(exp.left.dtype) == 0) && (getType(exp.right.dtype) == 0))) {
            System.out.println("Invalid operation expression at line " + exp.row + " and column " + exp.col);
            System.out.println("left: " + getType(exp.left.dtype) + " right: " + getType(exp.right.dtype));
            System.out.println("left: " + exp.left.dtype + " right: " + exp.right.dtype);
        }
    }

    public void visit(VarExp exp, int level) {
        exp.variable.accept(this, level);

        // DO CHECK HERE
        NameTy type = new NameTy(exp.row, exp.col, 0);
        exp.dtype = new SimpleDec(exp.row, exp.col, type, exp.variable.name);

        for (String name : symbolTable.keySet()) {
            for (int i = 0; i < symbolTable.get(name).size(); i++) {
                if (symbolTable.get(name).get(i).name.equals(exp.variable.name)) {
                    if (getType(symbolTable.get(name).get(i).def) == 1) {
                        System.out.println("Invalid integer value at line " + exp.row + " and column " + exp.col);
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
            System.out.println("Invalid function call at line " + exp.row + " and column " + exp.col);
        }

        // get temp
        // exp.dtype = new FunctionDec(exp.row, exp.col, temp.result, temp.func,
        // temp.params, temp.body);
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

        // DO CHECK HERE
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

        indent(level + 1);
        globalLevel++;
        // System.out.println(functionDec.func + ": (" +
        // getStringFromParams(functionDec.params) + ") -> "
        // + printType(functionDec.result.typ));
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
        NodeType node = new NodeType(functionDec.func, functionDec, globalLevel - 1);
        insertNodeToSymbolTable(node);
        indent(level);

        printLevel(level);
        indent(level);
        System.out.println("Leaving the function scope");
        removeLevel(level);
        globalLevel--;
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
            exp.dtype = exp.exp.dtype;
        }
    }

    public void visit(SimpleDec dec, int level) {
        NodeType node = new NodeType(dec.name, dec, level);
        insertNodeToSymbolTable(node);
        // indent(level + 1);
        // System.out.println(dec.name + ": " + printType(dec.typ.typ));
        // System.out.println("SimpleDec: " + dec.name);

    }

    public void visit(SimpleVar var, int level) {

    }

    public void visit(WhileExp exp, int level) {
        globalLevel++;
        level++;
        indent(level);
        System.out.println("Entering a new block of WhileEXP: ");
        exp.test.accept(this, level);
        if (exp.body != null)
            exp.body.accept(this, level);
        indent(level);
        printLevel(globalLevel);
        indent(level);
        System.out.println("Leaving the block of WhileEXp");
        removeLevel(globalLevel);

        globalLevel--;

    }

    public void visit(ArrayDec dec, int level) {
        NodeType node = new NodeType(dec.name, dec, level);
        insertNodeToSymbolTable(node);
        // indent(level + 1);
        // System.out.println(dec.name + ": " + printType(dec.typ.typ));
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
            for (int i = 0; i < symbolTable.get(name).size(); i++) {
                if (symbolTable.get(name).get(i).def instanceof FunctionDec) {
                    FunctionDec fd = (FunctionDec) symbolTable.get(name).get(i).def;

                    // length checks
                    if (checkArgs.size() == 0) {
                        if (fd.params == null) {// same lengths but no arguments, don't bother
                            return true;
                        } else {
                            System.out.println("param lengths of 0 arent the same");
                            return false;
                        }
                    }
                    if (getVarDecListLength(fd.params) == checkArgs.size()) {// same lengths
                        // check if list contains types in order
                        ArrayList<Integer> paramList = getParamTypes(fd.params);
                        for (int j = 0; j < paramList.size(); j++) {
                            if (!checkArgs.get(j).equals(paramList.get(j))) {
                                System.out.println("param " + j + " type mismatch");
                                return false;
                            }
                            return true;
                        }
                    } else {
                        System.out.println("function params of inequal length");
                        return false;
                    }

                }
            }
        }
        System.out.println("no matching function name");
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

    private void printLevel(int level) {
        // System.out.println(level +
        // "helloth!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ere");
        for (String name : symbolTable.keySet()) {
            for (int i = 0; i < symbolTable.get(name).size(); i++) {
                if (symbolTable.get(name).get(i).level == globalLevel) {
                    NodeType node = symbolTable.get(name).get(i);
                    indent(level);
                    if (node.def instanceof SimpleDec) {
                        System.out.println(node.name + ": " + printType(getType(node.def)));
                    } else if (node.def instanceof ArrayDec) {
                        ArrayDec ad = (ArrayDec) node.def;
                        System.out.println(node.name + "[" + ad.size.value + "] : " + printType(getType(node.def)));
                    } else if (node.def instanceof FunctionDec) {
                        FunctionDec fd = (FunctionDec) node.def;
                        System.out.println(node.name + ": (" + getStringFromParams(fd.params) + ") -> "
                                + printType(fd.result.typ));
                    }
                }
            }
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
}