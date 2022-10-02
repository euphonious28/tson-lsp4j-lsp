package com.tson.lsp.utility;

import com.euph28.tson.antlr.TsonParser;
import com.euph28.tson.antlr.TsonParserBaseListener;
import com.euph28.tson.interpreter.Interpretation;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.tson.lsp.data.TSONData;
import com.tson.lsp.utility.tson.DirectContentProvider;
import org.antlr.v4.runtime.ParserRuleContext;
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
        interpretation.walkListener(new TsonParserBaseListener() {
            @Override
            public void enterComment(TsonParser.CommentContext ctx) {
                result.addAll(getEntries(ctx, SemanticTokenTypes.Comment, "", true));
            }

            @Override
            public void enterProperties(TsonParser.PropertiesContext ctx) {
                result.addAll(getEntries(ctx, SemanticTokenTypes.Parameter, "", true));
            }

            @Override
            public void enterKeyword(TsonParser.KeywordContext ctx) {
                result.addAll(getEntries(ctx, SemanticTokenTypes.Function, "", false));
            }
        });

        return result;
    }

    /**
     * Retrieve entries, split by lines if applicable
     *
     * @param ctx           Parser context
     * @param tokenType     Type of token for the entry
     * @param tokenModifier Token modifier for the entry
     * @param multiline     If content can be multiline
     * @return Entries from the context content
     */
    List<SemanticTokenEntry> getEntries(ParserRuleContext ctx, String tokenType, String tokenModifier, boolean multiline) {
        // Result object
        List<SemanticTokenEntry> result = new ArrayList<>();

        // Convert text into multiple lines
        String[] lines;

        // Split if multiline, otherwise save effort and use one line
        if (multiline) {
            lines = ctx.getText().split("\\r?\\n", -1);
        } else {
            lines = new String[]{ctx.getText()};
        }

        // Context details (needed for determining which line it is in multiline)
        int startLine = ctx.getStart().getLine() - 1;
        int endLine = startLine + lines.length - 1;     // Calculate end line by using start + split result

        // Split into multiple entries for each line (multiline scenario)
        // For char position, first line uses start position, the rest uses 0 (first char)
        // For length, use the split result
        for (int line = startLine; line <= endLine; line++) {
            result.add(new SemanticTokenEntry(
                    line,
                    line == startLine ? ctx.getStart().getCharPositionInLine() : 0,
                    lines[line - startLine].length(),
                    tokenType,
                    tokenModifier
            ));
        }

        return result;
    }
}
