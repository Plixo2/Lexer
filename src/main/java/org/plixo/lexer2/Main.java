package org.plixo.lexer2;

import com.plixo.util.FileUtil;
import org.plixo.lexer2.instructionset.Instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) {
        String lines = FileUtil.loadAsString("res/res.txt");
        Lexer lexer = new Lexer();
        List<Lexer.Token> tokens = new ArrayList<>();

        for (String line : lines.split(Pattern.quote(System.getProperty("line.separator")))) {

            List<Lexer.Token> tokenList = lexer.tokens(line);
            tokens.addAll(tokenList);
        }
        System.out.println(tokens);

        Ast ast = new Ast();
        ast.tree(tokens);

        Ast.Node startNode = ast.getStartNode();
        System.out.println(startNode);

        if (startNode != null) {
            String debug = startNode.toStringF();
            FileUtil.save(FileUtil.getFileFromName("res/debug.txt"), debug);
        }


        Instruction instruction = new Instruction();
        instruction.deb();

       // TreePrinter.print(startNode);
      //  String parse = tree.parse(startNode);
      //  System.out.println(parse);



        //Lexical Analysis
        //Syntactic Analysis
        //Semantic Analysis
        //intermediate code
        //optimisation
        //assembly code
        //object code
        //linking

        //push for pushing
        //pop for popping
        //ret for return
        //mov for coping
        //% stack base pointer or register (temporary variable)
        //-number (%stack) is a memory offset
        //$ for constants
        //no diff between <= and <
        //movl means mem location (by label) and offset
        //jump dependent of the last instruction
        //jump jumps to flags... else and endif
        //if else only inverts
        //while are two flags.. while and do

        //call for functions
        //call means allocate new memory
        //return means popping the mem stack
        //op code
        //all operations with a register or a memory or constant
        //all operations has a major bits and minor bits

        //flags?

        //move /store / copy
        //jump jump if , jump < , jump >

        //compare

        //add value to register
        //add register to register

        //store register into mem
        //store mem into mem
        //store register into register
        //store mem into register

        //print

        //push
        //pop

    }


    static class SymbolTable {
        //name , type , scope ("Local main" , "global")
    }


}
