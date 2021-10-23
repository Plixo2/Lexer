package org.plixo.lexer2;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    final static String typeNumber = "number";
    final static String typePlus = "+";
    final static String typeMinus = "-";
    final static String typeDiv = "/";
    final static String typeMul = "*";
    final static String typeMod = "%";


    final static String typeAssign = "=";
    final static String typeNot = "!";
    final static String typeSeparate = ",";
    final static String typeExtend = ":";
    final static String typeQuestion = "?";
    final static String typeOBracket = "(";
    final static String typeCBracket = ")";
    final static String typeOSegment = "{";
    final static String typeCSegment = "}";
    final static String typeIf = "if";
    final static String typeElse = "else";
    final static String typeVar = "var";
    final static String typeLoop = "loop";
    final static String typeBreak = "break";
    final static String typeLocal = "local";
    final static String typeCache = "pin";
    final static String typeFalse = "false";
    final static String typeTrue = "true";
    final static String typeString = "string";
    final static String typeClass = "class";
    final static String typeInline = "inline";
    final static String typeKey = "key";
    final static String typeRef = "ref";
    final static String typeMacro = "macro";
    final static String typeThis = "self";
    final static String typeReturn = "return";
    final static String typeEqual = "==";
    final static String typeNotEqual = "!=";
    final static String typeAnd = "&&";
    final static String typeSmaller = "<";
    final static String typeGreater = ">";
    final static String typeSmallerEquals = "<=";
    final static String typeGreaterEquals = ">=";
    final static String typeOr = "||";

    int charPointer;
    char currentChar;
    byte[] bytes;

    public List<Token> tokens(String lines) {
        List<Token> tokens = new ArrayList<>();
        if (lines.isEmpty()) {
            return tokens;
        }
        charPointer = 0;
        bytes = lines.getBytes(StandardCharsets.UTF_8);

        advance();

        while (charsLeft()) {

            if (currentChar == ' ') {
                advance();
            } else if (currentChar == '+') {
                tokens.add(new Token(typePlus));
                advance();
            } else if (currentChar == '-') {
                tokens.add(new Token(typeMinus));
                advance();
            } else if (currentChar == '*') {
                tokens.add(new Token(typeMul));
                advance();
            } else if (currentChar == '/') {
                tokens.add(new Token(typeDiv));
                advance();
            } else if (currentChar == '%') {
                tokens.add(new Token(typeMod));
                advance();
            } else if (currentChar == '(') {
                tokens.add(new Token(typeOBracket));
                advance();
            } else if (currentChar == ')') {
                tokens.add(new Token(typeCBracket));
                advance();
            } else if (currentChar == ':') {
                tokens.add(new Token(typeExtend));
                advance();
            } else if (currentChar == '{') {
                tokens.add(new Token(typeOSegment));
                advance();
            } else if (currentChar == '}') {
                tokens.add(new Token(typeCSegment));
                advance();
            } else if (currentChar == ',') {
                tokens.add(new Token(typeSeparate));
                advance();
            } else if (currentChar == '?') {
                tokens.add(new Token(typeQuestion));
                advance();
            } else if (currentChar == '!') {
                if( peek("!=",false)) {
                    tokens.add(new Token(typeNotEqual));
                } else {
                    tokens.add(new Token(typeNot));
                }

                advance();
            }else if (currentChar == '<') {
                if( peek("<=",false)) {
                    tokens.add(new Token(typeSmallerEquals));
                } else {
                    tokens.add(new Token(typeSmaller));
                }
                advance();
            } else if (currentChar == '>') {
                if( peek(">=",false)) {
                    tokens.add(new Token(typeGreaterEquals));
                } else {
                    tokens.add(new Token(typeGreater));
                }
                advance();
            } else if (currentChar == '=') {
                if( peek("==",false)) {
                    tokens.add(new Token(typeEqual));
                } else {
                    tokens.add(new Token(typeAssign));
                }
                advance();
            } else if (currentChar == '&' && peek("&&",false)) {
                tokens.add(new Token(typeAnd));
                advance();
            } else if (currentChar == '|' && peek("||",false)) {
                tokens.add(new Token(typeOr));
                advance();
            } else if (currentChar == '\"') {
                tokens.add(addString());
            } else if (currentChar == 'v' && peek("var")) {
                tokens.add(new Token(typeVar));
                advance();
            } else if (currentChar == 'c' && peek("class")) {
                tokens.add(new Token(typeClass));
                advance();
            } else if (currentChar == 'p' && peek("pin")) {
                tokens.add(new Token(typeCache));
                advance();
            } else if (currentChar == 'e' && peek("else")) {
                tokens.add(new Token(typeElse));
                advance();
            } else if (currentChar == 'i' && peek("if")) {
                tokens.add(new Token(typeIf));
                advance();
            } else if (currentChar == 'i' && peek("inline")) {
                tokens.add(new Token(typeInline));
                advance();
            } else if (currentChar == 'r' && peek("ref")) {
                tokens.add(new Token(typeRef));
                advance();
            } else if (currentChar == 'l' && peek("local")) {
                tokens.add(new Token(typeLocal));
                advance();
            } else if (currentChar == 't' && peek("this")) {
                tokens.add(new Token(typeThis));
                advance();
            } else if (currentChar == 'l' && peek("loop")) {
                tokens.add(new Token(typeLoop));
                advance();
            } else if (currentChar == 'b' && peek("break")) {
                tokens.add(new Token(typeBreak));
                advance();
            } else if (currentChar == 't' && peek("true")) {
                tokens.add(new Token(typeTrue));
                advance();
            } else if (currentChar == 'm' && peek("macro")) {
                tokens.add(new Token(typeMacro));
                advance();
            } else if (currentChar == 'r' && peek("return")) {
                tokens.add(new Token(typeReturn));
                advance();
            } else if (currentChar == 'f' && peek("false")) {
                tokens.add(new Token(typeFalse));
                advance();
            } else if (Character.isDigit(currentChar)) {
                tokens.add(addNumber());
            } else if (availableInKeyWord()) {
                tokens.add(keyword());
            } else {
                System.out.println("Unknown key:" + currentChar);
                advance();
            }
        }


        return tokens;
    }

    private Token addString() {
        String str = "";
        if (charsLeft()) {
            advance();
        }
        boolean hasEnded = false;
        while (charsLeft()) {
            if (currentChar == '\"') {
                advance();
                hasEnded = true;
                break;
            } else {
                str += String.valueOf(currentChar);
                advance();
            }
        }
        if (!hasEnded) {
            throw new TokenException("failed to parse a string: " + str);
        }

        return new Token(typeString, str);
    }
    private boolean peek(String toCheck) {
        return peek(toCheck,true);
    }

    private boolean peek(String toCheck , boolean check) {
        int length = toCheck.length();
        if (length <= 1) {
            throw new TokenException();
        }
        String current = currentChar + "";
        char last = '-';
        if (charsLeft(length)) {
            for (int i = 0; i < length - 1; i++) {
                current += peek(i);
            }
            last = peek(length - 1);
        } else if (charsLeft(length - 1)) {
            for (int i = 0; i < length - 1; i++) {
                current += peek(i);
            }
            last = ' ';
        }
        if (current.equals(toCheck) && (!availableInKeyWord(last) || !check)) {
            charPointer += length - 1;
            return true;
        }

        return false;
    }


    private Token addNumber() {

        String str = "";
        int pointCount = 0;

        while (charsLeft()) {
            if (currentChar == '.') {
                pointCount += 1;
                if (pointCount > 1) {
                    System.out.println("failed To parse");
                    throw new TokenException();
                }
                str += String.valueOf(currentChar);
                advance();
            } else if (Character.isDigit(currentChar)) {
                str += String.valueOf(currentChar);
                advance();
            } else {
                break;
            }
        }

        return new Token(typeNumber, str);
    }


    private Token keyword() {
        String str = "";

        while (charsLeft()) {
            if (availableInKeyWord()) {
                str += String.valueOf(currentChar);
                advance();
            } else {
                break;
            }
        }

        return new Token(typeKey, str);
    }

    private boolean availableInKeyWord() {
        return availableInKeyWord(currentChar);
    }

    private boolean availableInKeyWord(char currentChar) {
        String abc = "abcdefghijklmnopqrstuvwxyz";
        String number = "0123456789";
        return abc.contains("" + currentChar) || number.contains("" + currentChar);
    }

    private void advance() {
        currentChar = (char) bytes[Math.min(bytes.length - 1, charPointer)];
        charPointer += 1;
    }

    private char peek(int i) {
        return (char) bytes[Math.min(bytes.length - 1, charPointer + i)];
    }

    private boolean charsLeft() {
        return charPointer <= bytes.length;
    }

    private boolean charsLeft(int length) {
        return charPointer + length <= bytes.length;
    }

    static class Token {

        String tokenType;
        Object object = null;

        public Token(String token) {
            this.tokenType = token;
        }

        public Token(String token, Object object) {
            this.tokenType = token;
            this.object = object;
        }

        @Override
        public String toString() {
            if (object != null) {
                return object+"";
            }
            return tokenType;
        }

        public boolean is(String tokenType) {
            return this.tokenType.equals(tokenType);
        }
    }

}
