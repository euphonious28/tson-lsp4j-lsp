package com.tson.lsp;

import org.eclipse.lsp4j.SemanticTokenModifiers;
import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;

import java.util.ArrayList;
import java.util.List;

public class SemanticTokenProvider extends SemanticTokensWithRegistrationOptions {

    public SemanticTokenProvider(){
        List<String> tokenTypes = new ArrayList<>();
        List<String> tokenModifiers = new ArrayList<>();

        tokenTypes.add(SemanticTokenTypes.Function);

        SemanticTokensLegend tokensLegend = new SemanticTokensLegend(tokenTypes, tokenModifiers);
        this.setLegend(tokensLegend);
    }

}
