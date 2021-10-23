package org.plixo.lexer2;

public class SyntaxException extends RuntimeException {
    public SyntaxException() {

    }

    public SyntaxException(String msg) {
        super(msg);
    }
}
