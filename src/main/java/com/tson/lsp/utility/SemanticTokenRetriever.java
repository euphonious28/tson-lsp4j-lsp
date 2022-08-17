package com.tson.lsp.utility;

import com.euph28.tson.antlr.TsonParser;
import com.euph28.tson.antlr.TsonParserListener;
import com.euph28.tson.interpreter.Interpretation;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.tson.lsp.data.TSONData;
import com.tson.lsp.utility.tson.DirectContentProvider;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

import java.util.ArrayList;
import java.util.List;

public class SemanticTokenRetriever {
    public List<SemanticTokenEntry> getSemanticTokens(String content, TSONData tsonData, CancelChecker cancelChecker) {
        // Result object
        List<SemanticTokenEntry> result = new ArrayList<>();

        // Use TSON Interpreter to parse the content
        TSONInterpreter interpreter = tsonData.getTsonInterpreter();
        interpreter.addContentProvider(new DirectContentProvider());
        Interpretation interpretation = interpreter.interpret(content);

        // Check that interpretation is loaded
        if (interpretation == null || interpretation.hasError()) {
            // TODO: Log error that parsing failed
            return result;
        }

        // Check for cancel
        if (cancelChecker.isCanceled()) {
            return result;
        }

        // Retrieve keywords
        interpretation.walkListener(new TsonParserListener() {
            @Override
            public void enterFile(TsonParser.FileContext ctx) {

            }

            @Override
            public void exitFile(TsonParser.FileContext ctx) {

            }

            @Override
            public void enterEntry(TsonParser.EntryContext ctx) {

            }

            @Override
            public void exitEntry(TsonParser.EntryContext ctx) {

            }

            @Override
            public void enterComment(TsonParser.CommentContext ctx) {
                result.add(new SemanticTokenEntry(
                        ctx.getStart().getLine() - 1,       // Offset to start line from 0
                        ctx.getStart().getCharPositionInLine(),
                        ctx.getText().length(),
                        SemanticTokenTypes.Comment,
                        ""
                ));
            }

            @Override
            public void exitComment(TsonParser.CommentContext ctx) {

            }

            @Override
            public void enterStatement(TsonParser.StatementContext ctx) {

            }

            @Override
            public void exitStatement(TsonParser.StatementContext ctx) {

            }

            @Override
            public void enterProperties(TsonParser.PropertiesContext ctx) {
                result.add(new SemanticTokenEntry(
                        ctx.getStart().getLine() - 1,       // Offset to start line from 0
                        ctx.getStart().getCharPositionInLine(),
                        ctx.getText().length(),
                        SemanticTokenTypes.Parameter,
                        ""
                ));
            }

            @Override
            public void exitProperties(TsonParser.PropertiesContext ctx) {

            }

            @Override
            public void enterPropertiesMap(TsonParser.PropertiesMapContext ctx) {

            }

            @Override
            public void exitPropertiesMap(TsonParser.PropertiesMapContext ctx) {

            }

            @Override
            public void enterPropertiesKey(TsonParser.PropertiesKeyContext ctx) {

            }

            @Override
            public void exitPropertiesKey(TsonParser.PropertiesKeyContext ctx) {

            }

            @Override
            public void enterPropertiesValue(TsonParser.PropertiesValueContext ctx) {

            }

            @Override
            public void exitPropertiesValue(TsonParser.PropertiesValueContext ctx) {

            }

            @Override
            public void enterKeyword(TsonParser.KeywordContext ctx) {
                result.add(new SemanticTokenEntry(
                        ctx.getStart().getLine() - 1,       // Offset to start line from 0
                        ctx.getStart().getCharPositionInLine(),
                        ctx.getText().length(),
                        SemanticTokenTypes.Function,
                        ""
                ));
            }

            @Override
            public void exitKeyword(TsonParser.KeywordContext ctx) {

            }

            @Override
            public void enterValue(TsonParser.ValueContext ctx) {

            }

            @Override
            public void exitValue(TsonParser.ValueContext ctx) {

            }

            @Override
            public void visitTerminal(TerminalNode node) {

            }

            @Override
            public void visitErrorNode(ErrorNode node) {

            }

            @Override
            public void enterEveryRule(ParserRuleContext ctx) {

            }

            @Override
            public void exitEveryRule(ParserRuleContext ctx) {

            }
        });

        return result;
    }
}
