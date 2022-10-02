package com.tson.lsp.utility.semantictoken;

import org.eclipse.lsp4j.Diagnostic;

import java.util.List;

public class ParserException extends Exception{

    /**
     * Diagnostic issue
     */
    List<Diagnostic> diagnosticList;

    public ParserException(List<Diagnostic> diagnosticList) {
        this.diagnosticList =diagnosticList;
    }

    public List<Diagnostic> getDiagnosticList() {
        return diagnosticList;
    }
}
