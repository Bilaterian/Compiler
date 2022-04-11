* C-Minus Compilation to TM Code
* File: sort
* Standard prelude:
 12:    LD 6,  0(0)		load gp with maxaddr
 13:   LDA 5,  0(6)		Copy gp to fp
 14:    ST 0,  0(0)		Clear content at loc
* Jump around i/o routines here
* code for input routine
 16:    ST 0, -1(5)		store return
 17:    IN 0,0,0			input
 18:    LD 7, -1(5)		return to caller
* code for output routine
 19:    ST 0, -1(5)		store return
 20:    LD 0, -2(5)		load output value
 21:   OUT 0,0,0			output
 22:    LD 7, -1(5)		return to caller
 15:   LDA 7,  7(7)		jump around i/o code
* End of standard prelude
* processing global array var: x
* Processing function: minloc
* jump around function body here
 24:    ST 0, -1(5)		move return address from ac to retFO
* processing global simple dec: low
* processing global simple dec: high
* -> compound statement
* processing global simple dec: i
* processing global simple dec: x
* processing global simple dec: k
* -> op
* <- op
* -> op
* -> subs
* <- subs
* <- op
* -> op
* -> op
* -> IntExp
 25:   LDC 0,  1(0)		load const
* <-IntExp
* <- op
* <- op
* -> while
* while: jump after body comes back here
* -> op
* <- op
* while: jump to end belongs here
* -> compound statement
* -> if
* -> op
* -> subs
* <- subs
* <- op
* -> compound statement
* -> op
* -> subs
* <- subs
* <- op
* -> op
* <- op
* <- compound statement
* <- if
* -> op
* -> op
* -> IntExp
 26:   LDC 0,  1(0)		load const
* <-IntExp
* <- op
* <- op
* <- compound statement
* <- while
* -> return
* <- return
* <- compound statement
 27:    LD 7, -1(5)		return to caller
 23:   LDA 7,  4(7)		jump around minloc function
* Processing function: sort
* jump around function body here
 29:    ST 0, -1(5)		move return address from ac to retFO
* processing global simple dec: low
* processing global simple dec: high
* -> compound statement
* processing global simple dec: i
* processing global simple dec: k
* -> op
* <- op
* -> while
* while: jump after body comes back here
* -> op
* -> op
* -> IntExp
 30:   LDC 0,  1(0)		load const
* <-IntExp
* <- op
* <- op
* while: jump to end belongs here
* -> compound statement
* processing global simple dec: t
* -> op
* -> call of function: minloc
* <- call
* <- op
* -> op
* -> subs
* <- subs
* <- op
* -> op
* -> subs
* <- subs
* -> subs
* <- subs
* <- op
* -> op
* -> subs
* <- subs
* <- op
* -> op
* -> op
* -> IntExp
 31:   LDC 0,  1(0)		load const
* <-IntExp
* <- op
* <- op
* <- compound statement
* <- while
* <- compound statement
 32:    LD 7, -1(5)		return to caller
 28:   LDA 7,  4(7)		jump around sort function
* Processing function: main
* jump around function body here
 34:    ST 0, -1(5)		move return address from ac to retFO
* start of finale
 35:    ST 5, -8(5)		push ofp
 36:   LDA 5, -8(5)		push frame
 37:   LDA 0,  1(7)		load ac with ret ptr
 38:   LDA 7, -5(7)		jump to main loc
 39:    LD 5,  0(5)		pop frame
* End of execution.
 40:  HALT 0,0,0			
