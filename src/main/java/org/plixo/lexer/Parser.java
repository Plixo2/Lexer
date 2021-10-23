package org.plixo.lexer;

import java.util.List;

public class Parser {

    List<Lexer.Token> tokens;
    int index;
    Lexer.Token token;

    public Parser(List<Lexer.Token> tokens) {
        this.tokens = tokens;
        index = -1;
        token = null;
        advance();
    }

    BinNode parse() {
        return expr();
    }

    BinNode factor() {
        Lexer.Token token = this.token;
        if(this.token instanceof Lexer.PlusToken  ||  this.token instanceof Lexer.MinusToken) {
            advance();
            BinNode factor = factor();
            return new UnaryNode(token  , factor);
        }
        else if(this.token instanceof Lexer.NumberToken) {
            advance();
            return new NumberNode(token);
        } else if(this.token instanceof Lexer.OBracketToken) {
            advance();
            BinNode expr = expr();
            if(this.token instanceof Lexer.CBracketToken) {
                advance();
                return expr;
            }
        }

        return null;
    }

    BinNode term() {
        BinNode left = factor();
        while((this.token instanceof Lexer.MulToken) || (this.token instanceof Lexer.DivToken)) {
            Lexer.Token token = this.token;
            advance();
            BinNode right = factor();
            left = new BinNode(token , left, right);
        }
        return left;
    }

    BinNode expr() {
        BinNode left = term();
        while((this.token instanceof Lexer.PlusToken) || (this.token instanceof Lexer.MinusToken)) {
            Lexer.Token token = this.token;
            advance();
            BinNode right = term();
            left = new BinNode(token , left, right);
        }
        return left;
    }



    void advance() {
        index += 1;
        if (index < tokens.size()) {
            token = tokens.get(index);
        }
    }


    static class BinNode {
        BinNode left;
        BinNode right;
        Lexer.Token token;

        public BinNode(Lexer.Token token, BinNode left, BinNode right) {
            this.token = token;
            this.left = left;
            this.right = right;
        }
        public BinNode() {

        }

        @Override
        public String toString() {
            return "[" + left + ", "+ token +", " + right + "]";
        }
    }

    static class NumberNode extends BinNode {
        Lexer.Token token;
        public NumberNode(Lexer.Token token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return token.toString();
        }
    }

    static class UnaryNode extends BinNode {
        Lexer.Token token;
        BinNode node;
        public UnaryNode(Lexer.Token token , BinNode node) {
            this.token = token;
            this.node = node;
        }


        @Override
        public String toString() {
            return "["+ token +"," + node +"]";
        }
    }

}
