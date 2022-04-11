
import java.util.ArrayList;
import java.util.HashMap;

import absyn.*;

public class CodeGenerator implements AbsynVisitor {

    final static int SPACES = 4;
    final int IADDR_SIZE = 1024;
    final int DADDR_SIZE = 1024;
    final int NO_REGS = 8;
    final int PC_REG = 7;
    // registers
    final int ofpFO = 0;
    final int retOF = -1;
    final int initOF = -2;

    final int ac = 0;
    final int ac1 = 1;
    final int fp = 5;
    final int gp = 6;
    final int pc = 7;
    int mainEntry;
    int globalOffset;

    int emitLoc;
    int highEmitLoc;
    int tempOffset;

    HashMap<String, ArrayList<NodeType>> symbolTable;

    public CodeGenerator() {
        globalOffset = 0;
        emitLoc = 0;
        highEmitLoc = 0;
        mainEntry = 0;
        tempOffset = 0;
    }

    public int emitSkip(int distance) {
        int i = emitLoc;
        emitLoc += distance;

        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
        return i;
    }

    public void emitBackup(int loc) {
        if (loc > highEmitLoc) {
            emitComment("BUG in emitBackup");
        }
        emitLoc = loc;
    }

    public void emitRestore() {
        emitLoc = highEmitLoc;
    }

    public void emitRM_Abs(String op, int r, int a, String comment) {
        System.out.printf("%3d: %5s %d,%3d(%d)\t\t%s%n", emitLoc, op, r, a - (emitLoc + 1), pc, comment);
        emitLoc++;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    public void emitComment(String comment) {
        System.out.println("* " + comment);
    }

    public void emitRM(String op, int r, int d, int s, String comment) {
        System.out.printf("%3d: %5s %d,%3d(%d)\t\t%s%n", emitLoc, op, r, d, s, comment);
        emitLoc++;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    // Print a registers only instruction
    public void emitRO(String op, int r, int s, int t, String comment) {
        System.out.printf("%3d: %5s %d,%d,%d\t\t\t%s%n", emitLoc, op, r, s, t, comment);
        emitLoc++;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    public void visit(Absyn trees, String fileName) {
        emitComment("C-Minus Compilation to TM Code");
        emitComment("File: " + fileName);
        emitComment("Standard prelude:");
        emitRM("LD", gp, 0, 0, "load gp with maxaddr");
        emitRM("LDA", fp, 0, gp, "copy gp to fp");
        emitRM("ST", 0, 0, 0, "clear content at loc");
        int savedLoc = emitSkip(1);

        emitComment("Jump around i/o routines here");
        emitComment("code for input routine");
        emitRM("ST", 0, -1, fp, "store return");
        emitRO("IN", 0, 0, 0, "input");
        emitRM("LD", pc, -1, fp, "return to caller");
        emitComment("code for output routine");
        emitRM("ST", 0, -1, fp, "store return");
        emitRM("LD", 0, -2, fp, "load output value");
        emitRO("OUT", 0, 0, 0, "output");
        emitRM("LD", pc, -1, fp, "return to caller");
        emitBackup(savedLoc);
        emitRM("LDA", pc, 7, pc, "jump around i/o code");
        emitRestore();
        emitComment("End of standard prelude");

        globalOffset = initOF;

        visit((DecList) trees, 0, false);
        if (mainEntry == 0) {
            // THROW AN ERROR
            emitRO("HALT", 0, 0, 0, "");
        }
        emitComment("start of finale");
        emitRM("ST", fp, globalOffset + ofpFO, fp, "push ofp");
        emitRM("LDA", fp, globalOffset, fp, "push frame");
        emitRM("LDA", ac, 1, pc, "load ac with ret ptr");
        emitRM_Abs("LDA", pc, mainEntry, "jump to main loc");
        emitRM("LD", fp, ofpFO, fp, "pop frame");
        emitComment("End of execution.");
        emitRO("HALT", 0, 0, 0, "");

    }

    public void visit(ExpList expList, int offset, Boolean isAddress) {
        while (expList != null) {
            try {
                expList.head.accept(this, offset, false);
                expList = expList.tail;
            } catch (Exception e) {
                expList = expList.tail;
            }
        }
    }

    public void visit(AssignExp exp, int offset, Boolean isAddress) {
        emitComment("-> op");

        if (exp.lhs != null) {
            exp.lhs.accept(this, offset, false);
        }
        exp.rhs.accept(this, offset, false);
        emitComment("<- op");

    }

    public void visit(IfExp exp, int offset, Boolean isAddress) {
        emitComment("-> if");
        exp.test.accept(this, offset, false);
        // int savedLoc = emitSkip(1);
        exp.thenpart.accept(this, offset + 1, false);
        // int savedLoc2 = emitSkip(0);
        // emitBackup(savedLoc);
        // emitRM_Abs("JEQ", 0, savedLoc2, "if: jump to else part");
        // emitRestore();

        if (exp.elsepart != null) {
            // emitComment("if: jump to else belongs here");
            exp.elsepart.accept(this, offset + 1, false);
        }
        emitComment("<- if");

    }

    public void visit(IntExp exp, int offset, Boolean isAddress) {

        NameTy type = new NameTy(exp.row, exp.col, 0);
        exp.dtype = new SimpleDec(exp.row, exp.col, type, exp.value);
        emitComment("-> IntExp");

        try {
            int tester = Integer.parseInt(exp.value);
            emitRM("LDC", ac, tester, 0, "load const");
        } catch (Exception e) {
            // System.err.println("Invalid integer expression at line " + exp.row + " and
            // column " + exp.col);
        }
        emitComment("<-IntExp");

    }

    public void visit(OpExp exp, int offset, Boolean isAddress) {
        emitComment("-> op");

        exp.left.accept(this, offset, false);
        exp.right.accept(this, offset, false);
        emitComment("<- op");

        // DO CHECK HERE

    }

    public void visit(VarExp exp, int offset, Boolean isAddress) {
        exp.variable.accept(this, offset + 1, false);

        // DO CHECK HERE

    }

    public void visit(CallExp exp, int offset, Boolean isAddress) {
        ArrayList<Integer> checkArgs = new ArrayList<Integer>();
        emitComment("-> call of function: " + exp.func);

        ExpList args = exp.args;
        while (args != null) {
            try {
                args.head.accept(this, offset, false);
                args = args.tail;
            } catch (Exception e) {
                args = args.tail;
            }
        }
        emitComment("<- call");

        // DO CHECK HERE

        // get temp

    }

    public void visit(CompoundExp exp, int offset, Boolean isAddress) {
        emitComment("-> compound statement");

        VarDecList decs = exp.decs;
        while (decs != null) {
            try {
                decs.head.accept(this, offset + 1, false);
                decs = decs.tail;
            } catch (Exception e) {
                decs = decs.tail;
            }
        }
        ExpList exps = exp.exps;
        while (exps != null) {
            try {
                exps.head.accept(this, offset + 1, false);
                exps = exps.tail;
            } catch (Exception e) {
                exps = exps.tail;
            }
        }
        emitComment("<- compound statement");

    }

    public void visit(VarDecList varDecList, int offset, Boolean isAddress) {
        while (varDecList != null) {
            try {
                // int dec
                if (varDecList.head instanceof SimpleDec) {
                    offset--;
                    if (varDecList.head.nestLevel == 0) {
                        globalOffset--;
                    }
                } else { // array dec
                    int arraySize = 0;
                    // if (((ArrayDec) varDecList.head).size.value != null
                    // && ((ArrayDec) varDecList.head).size.value != "") {
                    arraySize = Integer.parseInt(((ArrayDec) varDecList.head).size.value);
                    // System.out.println("AMONGUSSSSSSSSSSSSSSSSSS " + arraySize);
                    // System.out.println("YOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                    // System.out.println("SIZEEEEEEEEEEEEEEEEEE " + ((ArrayDec)
                    // varDecList.head).size.value);
                    // } else {
                    // System.out.println("SUSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                    // }
                    offset = offset - arraySize - 1;
                    if (varDecList.head.nestLevel == 0) {
                        globalOffset = globalOffset - arraySize - 1;
                    }

                }

                varDecList.head.accept(this, offset, false);
                varDecList = varDecList.tail;
            } catch (Exception e) {
                varDecList = varDecList.tail;
            }
        }
    }

    public void visit(DecList decList, int offset, Boolean isAddress) {

        while (decList != null) {
            try {
                decList.head.accept(this, offset, false);
                decList = decList.tail;
            } catch (Exception e) {
                decList = decList.tail;
            }
        }

    }

    public void visit(FunctionDec functionDec, int offset, Boolean isAddress) {

        emitComment("Processing function: " + functionDec.func);
        emitComment("jump around function body here");

        int savedLoc = emitSkip(1); // saving current instuction location for backpatching;

        functionDec.funaddr = emitLoc; // next instuctions address should become the new adress
        if (functionDec.func.toLowerCase().equals("main")) {
            // System.out.println("WOKEEEEEEE");
            mainEntry = functionDec.funaddr;
        }
        emitRM("ST", ac, retOF, fp, "move return address from ac to retFO");

        functionDec.params.accept(this, offset, false);
        VarDecList params = functionDec.params;
        int pcount = 0;
        while (params != null) {
            pcount++;
            params = params.tail;
        }

        functionDec.body.accept(this, offset - pcount, false);// body offsets start after params

        // At the end of the function, return to the caller
        emitRM("LD", pc, retOF, fp, "return to caller");

        // At this point, all instructions have been printed for the function and we
        // know how far to jump
        // So complete the backpatching for the jump around the function
        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "jump around " + functionDec.func + " function");
        emitRestore();

    }

    public void visit(IndexVar indexVar, int offset, Boolean isAddress) { // TODO
        emitComment("-> subs");

        indexVar.index.accept(this, offset, isAddress);
        emitComment("<- subs");

    }

    public void visit(NameTy nameTy, int offset, Boolean isAddress) {

    }

    public void visit(NilExp exp, int offset, Boolean isAddress) {

    }

    public void visit(ReturnExp exp, int offset, Boolean isAddress) {
        emitComment("-> return");

        if (exp.exp != null) {
            exp.exp.accept(this, offset + 1, false);
            // DO CHECK HERE
            // look for the latest function call
            exp.dtype = exp.exp.dtype;
        }
        emitComment("<- return");

    }

    public void visit(SimpleDec dec, int offset, Boolean isAddress) {
        dec.offset = offset;
        if (dec.nestLevel == 0) {
            emitComment("processing global simple dec: " + dec.name);
        } else {
            emitComment("processing local simple dec: " + dec.name);
        }
    }

    public Boolean containsDec(String name) {
        if (symbolTable.get(name) != null && symbolTable.get(name).isEmpty() != true) {
            return true;
        }
        return false;
    }

    public void visit(SimpleVar var, int offset, Boolean isAddress) {

    }

    public void visit(WhileExp exp, int offset, Boolean isAddress) {
        // offset++;
        emitComment("-> while");
        emitComment("while: jump after body comes back here");
        // int savedLoc = emitSkip(0);

        if (exp.test != null) {
            exp.test.accept(this, offset + 1, false);
            emitComment("while: jump to end belongs here");
        }
        // int savedLoc2 = emitSkip(1);

        if (exp.body != null)
            exp.body.accept(this, offset + 1, false);

        // emitRM_Abs("LDA", pc, savedLoc, "while: absolute jump to test");
        // int savedLoc3 = emitSkip(0);
        // emitBackup(savedLoc2);
        // emitRM_Abs("JEQ", 0, savedLoc3, "while: jump to end");
        // emitRestore();

        emitComment("<- while");
    }

    public void visit(ArrayDec dec, int offset, Boolean isAddress) {
        if (dec.nestLevel == 0) {
            emitComment("processing global array var: " + dec.name);
        } else {
            emitComment("processing local array var: " + dec.name);
        }
        dec.offset = offset;

    }

}
