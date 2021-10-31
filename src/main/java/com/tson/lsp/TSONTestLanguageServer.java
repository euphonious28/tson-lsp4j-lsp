package com.tson.lsp;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

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

        // Update the server capabilities
        initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        CompletionOptions completionOptions = new CompletionOptions();
        initializeResult.getCapabilities().setCompletionProvider(completionOptions);

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
