package org.plixo.lexer2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class Ast {

    int tokenPointer;
    List<Lexer.Token> tokens;
    Lexer.Token token;

    public void tree(List<Lexer.Token> tokens) {
        this.tokens = tokens;
        tokenPointer = -1;
        advance();
    }


    public Node getStartNode() {
        return classNodes();
    }

    private Node classNodes() {
        MultiNode base = new MultiNode();
        while (true) {
            Lexer.Token token = this.token;
            if (token.is(Lexer.typeClass)) {
                advance();
                Lexer.Token key = this.token;
                if (key.is(Lexer.typeKey)) {
                    advance();
                    if (this.token.is(Lexer.typeOBracket)) {
                        advance();
                        Node parameter = parameterNode();
                        if (this.token.is(Lexer.typeCBracket)) {
                            advance();
                            if (this.token.is(Lexer.typeOSegment)) {
                                advance();

                                ClassNode classNode = new ClassNode(parameter , key, normalCode());
                                advance();
                                base.add(classNode);
                                continue;
                            }  else {
                                throw new SyntaxException("no opening segment");
                            }
                        } else {
                            throw new SyntaxException("no closing bracket");
                        }

                    } else {
                        throw new SyntaxException("no opening bracket");
                    }

                } else {
                    throw new SyntaxException("no key");
                }
            }
            break;

        }
        return base;
    }


    private Node normalCode() {
        List<Node> nodes = new ArrayList<>();
        while (!this.token.is(Lexer.typeCSegment)) {
            if (this.token.is(Lexer.typeVar)) {
                advance();
                Lexer.Token key = this.token;
                if (key.is(Lexer.typeKey)) {
                    advance();
                    if (this.token.is(Lexer.typeAssign)) {
                        advance();
                        VarNode varNode = new VarNode(key, expr());
                        nodes.add(varNode);
                    } else {
                        throw new SyntaxException("no assign");
                    }
                } else
                    throw new SyntaxException("no key");
            } else if (this.token.is(Lexer.typeIf)) {
                Lexer.Token ifToken = this.token;

                advance();
                if (this.token.is(Lexer.typeOBracket)) {
                    advance();
                    Node expr = boolNode();
                    advance();
                    if (this.token.is(Lexer.typeOSegment)) {
                        advance();
                        Node children = normalCode();
                        Node infoNode = new Node(null, new Lexer.Token("info"), expr);
                        nodes.add(new IfNode(children, ifToken, infoNode));
                        advance();
                        Lexer.Token elseToken = this.token;
                        if (elseToken.is(Lexer.typeElse)) {
                            advance();
                            if (this.token.is(Lexer.typeOSegment)) {
                                advance();
                                Node node = normalCode();
                                infoNode.left = new ElseNode(elseToken, node);

                            } else {
                                throw new SyntaxException("no parenthesis");
                            }
                        }

                    } else {
                        throw new SyntaxException("no parenthesis");
                    }
                } else {
                    throw new SyntaxException("no parenthesis");
                }


            }
        }
        MultiNode multiNode = new MultiNode();
        multiNode.addAll(nodes);
        return multiNode;
    }

    private Node parameterNode() {

        MultiNode multiNode = new MultiNode();

        while (this.token.is(Lexer.typeKey)) {
            multiNode.add(new KeyNode(this.token));
            advance();
            if (this.token.is(Lexer.typeSeparate)) {
                advance();
            }
        }


        return multiNode;
    }

    private Node boolNode() {
        return n(() -> this.token.is(Lexer.typeAnd) || this.token.is(Lexer.typeOr) || this.token.is(Lexer.typeEqual), this::numberComparison);
    }

    private Node numberComparison() {
        return n(() -> this.token.is(Lexer.typeEqual) || this.token.is(Lexer.typeNotEqual) ||
                this.token.is(Lexer.typeSmaller) || this.token.is(Lexer.typeSmallerEquals) ||
                this.token.is(Lexer.typeGreater) || this.token.is(Lexer.typeGreaterEquals), this::expr);
    }


    private Node factor() {
        Lexer.Token token = this.token;
        if (this.token.is(Lexer.typePlus) || this.token.is(Lexer.typeMinus)) {
            advance();
            Node factor = factor();
            return new UnaryNode(token, factor);
        } else if (this.token.is(Lexer.typeNumber)) {
            advance();
            return new NumberNode(token);
        } else if (this.token.is(Lexer.typeKey)) {
            advance();
            return new KeyNode(token);
        } else if (this.token.is(Lexer.typeTrue)) {
            advance();
            return new BoolNode(token);
        } else if (this.token.is(Lexer.typeFalse)) {
            advance();
            return new BoolNode(token);
        } else if (this.token.is(Lexer.typeOBracket)) {
            advance();
            Node expr = expr();
            if (this.token.is(Lexer.typeCBracket)) {
                advance();
                return expr;
            }
        } else {
            throw new SyntaxException("failed to parse: " + this.token);
        }


        return null;
    }

    private Node term() {
        return n(() -> this.token.is(Lexer.typeMul) || this.token.is(Lexer.typeDiv) || this.token.is(Lexer.typeMod), this::factor);
    }

    private Node expr() {
        return n(() -> this.token.is(Lexer.typePlus) || this.token.is(Lexer.typeMinus), this::term);
    }

    private Node n(Supplier<Boolean> predicate, Supplier<Node> toCall) {
        Node left = toCall.get();
        while (predicate.get()) {
            Lexer.Token token = this.token;
            advance();
            Node right = toCall.get();
            left = new Node(left, token, right);
        }
        return left;
    }

    private void advance() {
        tokenPointer += 1;
        if (tokenPointer < tokens.size()) {
            token = tokens.get(tokenPointer);
        }
        if (tokenPointer - 1 >= tokens.size()) {
            throw new SyntaxException("failed to parse: \"" + token + "\"");
        }

    }


    public static class Node {
        public Node left;
        public Lexer.Token terminal;
        public Node right;

        public Node(Node left, Lexer.Token terminal, Node right) {
            this.left = left;
            this.terminal = terminal;
            this.right = right;
        }

        @Override
        public String toString() {
            String str = "[";

            if (left != null) {
                str += left.toString() + ", ";
            }

            str += getDescription();
            if (right != null) {
                str += ", " + right.toString();
            }
            return str + "]";

        }

        public String getDescription() {
            return terminal.toString() + " " + getClass().getSimpleName();
        }

        public StringBuilder toString(StringBuilder prefix, boolean isTail, StringBuilder sb) {
            if (right != null) {
                right.toString(new StringBuilder().append(prefix).append(isTail ? "│   " : "    "), false, sb);
            }
            sb.append(prefix).append(isTail ? "└── " : "┌── ").append(getDescription()).append("\n");
            if (left != null) {
                left.toString(new StringBuilder().append(prefix).append(isTail ? "    " : "│   "), true, sb);
            }
            return sb;
        }

        public String toStringF() {
            return this.toString(new StringBuilder(), true, new StringBuilder()).toString();
        }
    }

    public static class ValueNode extends Node {
        public ValueNode(Lexer.Token terminal) {
            super(null, terminal, null);
        }
    }

    public static class NumberNode extends ValueNode {
        public NumberNode(Lexer.Token terminal) {
            super(terminal);
        }
    }

    public static class BoolNode extends ValueNode {
        public BoolNode(Lexer.Token terminal) {
            super(terminal);
        }
    }

    public static class KeyNode extends ValueNode {
        public KeyNode(Lexer.Token terminal) {
            super(terminal);
        }
    }

    public static class UnaryNode extends Node {
        public UnaryNode(Lexer.Token terminal, Node factor) {
            super(null, terminal, factor);
        }
    }

    public static class IfNode extends Node {
        public IfNode(Node children, Lexer.Token terminal, Node condition) {
            super(children, terminal, condition);
        }
    }

    public static class ElseNode extends Node {
        public ElseNode(Lexer.Token terminal, Node children) {
            super(children, terminal, null);
        }
    }


    public static class MultiNode extends Node {

        List<Node> children = new ArrayList<>();

        public MultiNode() {
            super(null, null, null);
        }

        public void add(Node node) {
            this.children.add(node);
        }

        public void addAll(Collection<Node> nodeCollections) {
            this.children.addAll(nodeCollections);
        }

        @Override
        public String getDescription() {
            return children.toString();
        }

        @Override
        public StringBuilder toString(StringBuilder prefix, boolean isTail, StringBuilder sb) {
            if (children.size() > 0) {
                int toIndex = children.size() / 2;
                List<Node> nodes = children.subList(0, toIndex);
                List<Node> nodes2 = children.subList(toIndex, children.size());
                for (int i = 0; i < nodes.size(); i++) {
                    Node child = nodes.get(i);
                    if (child != null) {
                        child.toString(new StringBuilder().append(prefix).append(isTail ? "│   " : "    "), (i == nodes.size() - 1) && i != 0, sb);
                    }
                }

                sb.append(prefix).append(isTail ? "└── " : "┌── ").append("[] ListNode").append("\n");
                for (Node child : nodes2) {
                    child.toString(new StringBuilder().append(prefix).append(isTail ? "    " : "│   "), true, sb);
                }
                return sb;
            } else {
                sb.append(prefix).append(isTail ? "└── " : "┌── ").append("[]").append("\n");
                return sb;
            }
        }

    }


    public static class ClassNode extends Node {

        public ClassNode(Node left , Lexer.Token terminal, Node right) {
            super(left, terminal, right);
        }
    }

    public static class VarNode extends Node {

        public VarNode(Lexer.Token terminal, Node right) {
            super(null, terminal, right);
        }
    }
}
