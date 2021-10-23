package org.plixo.lexer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    StringIndexer indexer;
    List<Token> tokens;

    public void parse(String equation) {
        tokens = new ArrayList<>();
        indexer = new StringIndexer(equation);
        indexer.advance();

        while (indexer.clamped()) {
            if (indexer.cachedCharacter == ' ') {
                indexer.advance();
            } else if (Character.isDigit(indexer.cachedCharacter)) {
                tokenNumber(indexer);
            } else if (isOperator(indexer.cachedCharacter)) {
                indexer.advance();
            } else if (indexer.cachedCharacter == '(') {
                tokens.add(new OBracketToken());
                indexer.advance();
            } else if (indexer.cachedCharacter == ')') {
                tokens.add(new CBracketToken());
                indexer.advance();
            } else {
                System.out.println("?" + indexer.cachedCharacter);
                break;
            }
        }

        System.out.println(tokens);
    }

    boolean isOperator(char ch) {
        if (ch == '+') {
            tokens.add(new PlusToken());
            return true;
        } else if (ch == '-') {
            tokens.add(new MinusToken());
            return true;
        } else if (ch == '*') {
            tokens.add(new MulToken());
            return true;
        } else if (ch == '/') {
            tokens.add(new DivToken());
            return true;
        }
        return false;
    }


    static class StringIndexer {
        int cachedPosition = -1;
        char cachedCharacter;
        String str;
        byte[] bytes;

        public StringIndexer(String str) {
            this.str = str;
            bytes = str.getBytes(StandardCharsets.UTF_8);
        }

        void advance() {
            cachedPosition += 1;
            cachedCharacter = (char) bytes[Math.min(cachedPosition, bytes.length - 1)];
        }

        boolean clamped() {
            return cachedPosition < bytes.length;
        }
    }


    void tokenNumber(StringIndexer indexer) {

        String str = "";
        int pointCount = 0;

        while (indexer.clamped()) {
            if (indexer.cachedCharacter == '.') {
                pointCount += 1;
                if (pointCount > 1) {
                    System.out.println("failed To parse");
                    break;
                }
                str += String.valueOf(indexer.cachedCharacter);
                indexer.advance();
            } else if (Character.isDigit(indexer.cachedCharacter)) {
                str += String.valueOf(indexer.cachedCharacter);
                indexer.advance();
            } else {
                break;
            }

        }

        tokens.add(new NumberToken(str));
    }

    static class PlusToken extends Token {
        @Override
        String print() {
            return "+";
        }
    }

    static class MinusToken extends Token {
        @Override
        String print() {
            return "-";
        }

    }

    static class DivToken extends Token {
        @Override
        String print() {
            return "/";
        }
    }

    static class MulToken extends Token {
        @Override
        String print() {
            return "*";
        }
    }

    static class OBracketToken extends Token {
        @Override
        String print() {
            return "(";
        }
    }

    static class CBracketToken extends Token {
        @Override
        String print() {
            return ")";
        }
    }

    static class NumberToken extends Token {
        boolean isFloat = false;
        Number number;

        public NumberToken(String string) {
            if (string.contains(".")) {
                isFloat = true;
            }

            if (isFloat) {
                number = Float.valueOf(string);
            } else {
                number = Integer.valueOf(string);
            }
        }

        @Override
        String print() {
            return number.toString();
        }

    }

    static abstract class Token {
        abstract String print();

        @Override
        public String toString() {
            return print();
        }
    }
    //heap
}
