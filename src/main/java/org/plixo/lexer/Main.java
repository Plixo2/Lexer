package org.plixo.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void maina(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(": ");
        String s = br.readLine();

        Lexer lexer = new Lexer();

        lexer.parse(s);

        Parser parser = new Parser(lexer.tokens);

        System.out.println(parser.parse());

        br.close();
    }


}
