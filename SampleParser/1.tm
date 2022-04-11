* C-Minus Compilation to TM Code
* File: 1
* Standard prelude:
  2:    LD 6,  0(0)		load gp with maxaddr
  3:   LDA 5,  0(6)		Copy gp to fp
  4:    ST 0,  0(0)		Clear content at loc
* Jump around i/o routines here
* code for input routine
  6:    ST 0, -1(5)		store return
  7:    IN 0,0,0			input
  8:    LD 7, -1(5)		return to caller
* code for output routine
  9:    ST 0, -1(5)		store return
 10:    LD 0, -2(5)		load output value
 11:   OUT 0,0,0			output
 12:    LD 7, -1(5)		return to caller
  5:   LDA 7,  7(7)		jump around i/o code
* End of standard prelude
* Processing function: main
* jump around function body here
 14:    ST 0, -1(5)		move return address from ac to retFO
* start of finale
 15:    ST 5,  0(5)		push ofp
 16:   LDA 5,  0(5)		push frame
 17:   LDA 0,  1(7)		load ac with ret ptr
 18:   LDA 7, -5(7)		jump to main loc
 19:    LD 5,  0(5)		pop frame
* End of execution.
 20:  HALT 0,0,0			
