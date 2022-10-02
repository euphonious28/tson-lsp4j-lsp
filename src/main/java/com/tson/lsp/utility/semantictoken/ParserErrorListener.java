package com.tson.lsp.utility.semantictoken;

import com.euph28.tson.interpreter.ErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.List;

public class ParserErrorListener extends ErrorListener {
    List<Diagnostic> diagnosticList = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
        diagnosticList.add(new Diagnostic(
                new Range(
                        new Position(line - 1, charPositionInLine),
                        new Position(line - 1, charPositionInLine)
                ),
                msg
        ));
    }

    public List<Diagnostic> getDiagnosticList() {
        return diagnosticList;
    }
}
