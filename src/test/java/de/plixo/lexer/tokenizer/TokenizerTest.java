package de.plixo.lexer.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenizerTest {

    private static Random random = new Random();

    private final int testAmount = 100;
    @Test
    public void tokenizer() {
        long lexer = 0;
        long generation = 0;
        long testing = 0;
        long charsCheckLexed = 0;
        for (int j = 0; j < testAmount; j++) {
            long ms = System.currentTimeMillis();
            final List<Token> tokens = new ArrayList<>();
            final int length = Token.values().length;
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 200; i++) {
                Token value = Token.values()[random.nextInt(length)];
                while (value == Token.WHITESPACE) {
                    value = Token.values()[random.nextInt(length)];
                }
                tokens.add(value);
                builder.append(generateFromToken(value));
                builder.append(" ");
            }
            charsCheckLexed += builder.length();
            generation += System.currentTimeMillis()-ms;
            ms = System.currentTimeMillis();

            List<TokenRecord<Token>> records = new ArrayList<>();
            Tokenizer.apply(builder.toString(), Arrays.asList(Token.values()), (token, data, from, to) -> {
                records.add(new TokenRecord<>(token, data, from, to));
            }, (token, subString) -> token.peek.asPredicate()
                    .test(subString), (token, subString) -> token.capture.asPredicate().test(subString));
            lexer += System.currentTimeMillis()-ms;
            ms = System.currentTimeMillis();

            for (int i = 1; i < records.size(); i += 2) {
                final TokenRecord<Token> record = records.get(i);
                assertEquals(record.token, Token.WHITESPACE);
            }
            records.removeIf(tokenTokenRecord -> tokenTokenRecord.token == Token.WHITESPACE);
            assertEquals(records.size(), tokens.size());
            for (int i = 0; i < records.size(); i++) {
                final TokenRecord<Token> record = records.get(i);
                final Token token = tokens.get(i);
                assertEquals(record.token, token);
            }
            testing += System.currentTimeMillis()-ms;
        }
        System.out.println("Took " + (lexer/(float) testAmount) + "ms per tokenizer for " + (charsCheckLexed/(float) testAmount) + " characters ");
        System.out.println("Took " + (testing/(float) testAmount) + "ms per test");
        System.out.println("Took " + (generation/(float) testAmount) + "ms per generation");



    }

    private String generateFromToken(Token token) {
        return switch (token) {
            case CONCRETE -> "!";
            case HIDDEN -> "?";
            case COMMENT -> "//";
            case OR -> "|";
            case ASSIGN -> ":=";
            case WHITESPACE -> "";
            case LITERAL -> {
                String str = "\"";
                for (int i = 0; i < 10 + random.nextInt(30); i++) {
                    final String ran = "abcdefghijklmnopqrstuvwxyz1246486.,->!^°<=";
                    str += ran.charAt(random.nextInt(ran.length()));
                }
                str += "\"";
                yield str;
            }
            case KEYWORD -> {
                String str = "";
                for (int i = 0; i < 10 + random.nextInt(30); i++) {
                    final String ran = "abcdefghijklmnopqrstuvwxyz";
                    str += ran.charAt(random.nextInt(ran.length()));
                }
                yield str;
            }
        };
    }

    public enum Token {
        KEYWORD("[a-zA-Z]", "\\w+$"),
        WHITESPACE("\\s", "\\s*"),
        ASSIGN(":=", "(:|:=)"),
        LITERAL("\"", "(" +
                "(\\\"[^\\\"]*\\\")|(\\\"[^\\\"]*))"),
        OR("\\|", "\\|"),
        HIDDEN("\\?", "\\?"),
        CONCRETE("\\!", "\\!"),
        COMMENT("//", "//"),


        ;
        public final Pattern peek;
        public final Pattern capture;

        Token(String peek, String capture) {
            this.peek = Pattern.compile("^" + peek, Pattern.MULTILINE);
            this.capture = Pattern.compile("^" + capture + "$", Pattern.MULTILINE);
        }


    }


    private static class TokenRecord<T> {
        public final T token;
        public final String data;
        public final int from;
        public final int to;

        public TokenRecord(T token, String data, int from, int to) {
            this.token = token;
            this.data = data;
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return "TokenRecord{" +
                    "token=" + token +
                    ", data='" + data + '\'' +
                    ", from=" + from +
                    ", to=" + to +
                    '}';
        }
    }
}