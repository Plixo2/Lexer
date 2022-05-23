package de.plixo.lexer;

import de.plixo.lexer.exceptions.UnknownRuleException;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class AutoLexer<T> {

    final BiPredicate<String, T> tokenTest;
    final Consumer<T> onError;

    public SyntaxNode<T> reverseRule(GrammarReader.RuleSet ruleSet, String entry, List<T> tokens) {
        final GrammarReader.Rule rule = ruleSet.findRule(entry);
        if (rule == null) {
            throw new UnknownRuleException("Unknown entry rule \"" + entry + "\"");
        }
        val stream = new TokenStream<>(tokens);
        val node = testRule(rule, stream);
        if (stream.hasEntriesLeft()) {
            return null;
        }
        return node;
    }

    private SyntaxNode<T> testRule(GrammarReader.Rule rule, TokenStream<T> stream) {
        for (GrammarReader.Sentence sentence : rule.sentences) {
            final int index = stream.index();
            val node = testSentence(sentence, stream);
            if (node == null) {
                stream.setIndex(index);
            } else {
                node.name = rule.name;
                node.data = stream.current();
                return node;
            }
        }
        return null;
    }

    private SyntaxNode<T> testSentence(GrammarReader.Sentence sentence, TokenStream<T> stream) {
        final List<SyntaxNode<T>> nodes = new ArrayList<>();
        for (int i = 0; i < sentence.entries.size(); i++) {
            final GrammarReader.Entry entry = sentence.entries.get(i);
            if (!stream.hasEntriesLeft()) {
                return null;
            }
            if (entry.isLiteral()) {
                final T token = stream.current();
                if (!tokenTest.test(entry.literal, token)) {
                    return null;
                }
                if (!entry.isHidden) {
                    nodes.add(genLeaf(token));
                }
                stream.consume();
            } else {
                if (entry.rule == null) {
                    throw new NullPointerException();
                }
                final SyntaxNode<T> child = testRule(entry.rule, stream);
                if (child == null) {
                    if (entry.isConcrete) {
                        final T token = stream.current();
                        onError.accept(token);
                        throw new UnknownRuleException("Failed to capture rule " + entry.rule.name);
                    }
                    return null;
                }
                nodes.add(child);
            }
        }

        return genNode(nodes);
    }

    private SyntaxNode<T> genLeaf(T data) {
        return new LeafNode(data);
    }

    private SyntaxNode<T> genNode(List<SyntaxNode<T>> list) {
        final SyntaxNode<T> syntaxNode = new SyntaxNode<>();
        syntaxNode.list = list;
        return syntaxNode;
    }

    public static class SyntaxNode<T> {
        public String name;
        public List<SyntaxNode<T>> list;
        public T data;
    }

    @RequiredArgsConstructor
    public class LeafNode extends SyntaxNode<T> {
        public final T data;
    }

}
