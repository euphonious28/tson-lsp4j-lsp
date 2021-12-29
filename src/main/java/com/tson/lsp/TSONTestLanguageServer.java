package com.tson.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TSONTestLanguageServer implements LanguageServer, LanguageClientAware {

    /* ----- VARIABLES ------------------------------ */
    private TSONTestTextDocumentService textDocumentService;
    private TSONTestWorkspaceService workspaceService;
    private LanguageClient languageClient;

    /**
     * Error code of the server. Will be used when exiting to state reason of exit
     */
    private int errorCode = 1;

    /* ----- CONSTRUCTOR ------------------------------ */
    public TSONTestLanguageServer() {
        this.textDocumentService = new TSONTestTextDocumentService();
        this.workspaceService = new TSONTestWorkspaceService();
    }

    /* ----- OVERRIDE: LanguageServer ------------------------------ */

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {

        languageClient.logMessage(new MessageParams(MessageType.Log, "Initializing server"));

        // Result item to return. Contains the server's capabilities
        InitializeResult initializeResult = new InitializeResult(new ServerCapabilities());

        // Capability: sync type
        initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);

        // Capability: code completion
        CompletionOptions completionOptions = new CompletionOptions();
        initializeResult.getCapabilities().setCompletionProvider(completionOptions);

        // Capability: Semantic tokens
        // Reference: https://code.visualstudio.com/api/language-extensions/semantic-highlight-guide
        // Reference: https://github.com/eclipse/lsp4j/pull/446
        // Reference: https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textDocument_semanticTokens
        SemanticTokensWithRegistrationOptions semanticOptions =
                new SemanticTokensWithRegistrationOptions(textDocumentService.getSemanticTokensLegend());
        semanticOptions.setFull(true);
        List<DocumentFilter> documentFilterList = new ArrayList<>();
        DocumentFilter tsonDocumentFilter = new DocumentFilter();
        tsonDocumentFilter.setLanguage("tson");
        tsonDocumentFilter.setScheme("file");
        documentFilterList.add(tsonDocumentFilter);
        semanticOptions.setDocumentSelector(documentFilterList);
        initializeResult.getCapabilities().setSemanticTokensProvider(semanticOptions);

        return CompletableFuture.completedFuture(initializeResult);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        // Set error code to 0 as it is a graceful shutdown
        errorCode = 0;
        return null;
    }

    @Override
    public void exit() {
        // Exit using the stored error code
        System.exit(errorCode);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /* ----- OVERRIDE: LanguageClientAware ------------------------------ */

    @Override
    public void connect(LanguageClient client) {
        this.languageClient = client;
        this.textDocumentService.setClient(client);
    }
}
