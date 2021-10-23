package org.plixo.lexer2.instructionset;

public class Instruction {

    final static int Store = 0x01;
    final static int Jump = 0x02;

    final static int Add = 0x80;


    final static int MEMORY = 0x1;
    final static int CONSTANT = 0x2;

    public void deb() {
        in(Jump, MEMORY);
    }

    /*         v type code
        00000 000
          ^ operator code
    */

    final static int shift = 5;

    void in(int opcode, int type) {
        int full = opcode << shift | type;
        int ogcode = full >> shift;
        int ogtype = full & ((2 << shift) - 1);
        //   System.out.println(Integer.toBinaryString(ogtype));
    }
}
