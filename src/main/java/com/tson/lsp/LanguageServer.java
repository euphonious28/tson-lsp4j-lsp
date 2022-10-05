package com.tson.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * TSON Language Server
 */
public class LanguageServer implements org.eclipse.lsp4j.services.LanguageServer, LanguageClientAware {

    /* ----- VARIABLES ------------------------------ */
    private final TextDocumentService textDocumentService;
    private final WorkspaceService workspaceService;
    private LanguageClient languageClient;

    /**
     * Error code of the server. Will be used when exiting to state reason of exit
     */
    private int errorCode = 1;

    /* ----- CONSTRUCTOR ------------------------------ */
    public LanguageServer() {
        this.textDocumentService = new TextDocumentService();
        this.workspaceService = new WorkspaceService();
    }

    /* ----- OVERRIDE: (LSP4J)LanguageServer ------------------------------ */

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        // Log initialization
        languageClient.logMessage(new MessageParams(MessageType.Log, "Initializing server"));

        // Result item to return. Contains the server's capabilities
        InitializeResult initializeResult = new InitializeResult(new ServerCapabilities());

        // Capability: Sync type
        initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);

        // Capability: Code completion
        initializeResult.getCapabilities().setCompletionProvider(new CompletionOptions());

        // Capability: Semantic tokens
        initializeResult.getCapabilities().setSemanticTokensProvider(getSemanticTokenCapabilities());

        // Return generated capabilities
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
    public org.eclipse.lsp4j.services.TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public org.eclipse.lsp4j.services.WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    /* ----- OVERRIDE: LanguageClientAware ------------------------------ */
    @Override
    public void connect(LanguageClient client) {
        this.languageClient = client;
        this.textDocumentService.setClient(client);
    }

    /* ----- METHODS: CAPABILITIES ------------------------------ */

    /**
     * Get capabilities related to the Semantic Tokens. Developed with the following sources: <br/>
     * - https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textDocument_semanticTokens <br/>
     * - https://code.visualstudio.com/api/language-extensions/semantic-highlight-guide <br/>
     * - https://github.com/eclipse/lsp4j/pull/446 <br/>
     *
     * @return Semantic Tokens capabilities
     */
    SemanticTokensWithRegistrationOptions getSemanticTokenCapabilities() {
        // Return result
        SemanticTokensWithRegistrationOptions semanticOptions =
                new SemanticTokensWithRegistrationOptions(textDocumentService.getSemanticTokensLegend());

        // Capabilities
        semanticOptions.setFull(true);

        // Document types
        List<DocumentFilter> documentFilterList = new ArrayList<>();
        documentFilterList.add(new DocumentFilter(
                "tson",
                "file",
                ""
        ));
        semanticOptions.setDocumentSelector(documentFilterList);

        // Return
        return semanticOptions;
    }
}
