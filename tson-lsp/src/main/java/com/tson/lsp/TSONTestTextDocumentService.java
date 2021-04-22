package com.tson.lsp;

import com.tson.parser.Parser;
import com.tson.parser.Statement;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TSONTestTextDocumentService implements TextDocumentService {

    /* ----- VARIABLES ------------------------------ */

    private LanguageClient client;

    /* ----- SETTER ------------------------------ */

    public void setClient(LanguageClient client) {
        this.client = client;
    }

    /* ----- OVERRIDE: TextDocumentService ------------------------------ */

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {

        // TODO: Implement a proper autocomplete using the (CompletionParams)position
        client.logMessage(new MessageParams(MessageType.Log, "Calculating completion"));
        return CompletableFuture.supplyAsync(() -> {
            // List of completion items to return
            List<CompletionItem> completionItemList = new ArrayList<>();

            CompletionItem completionItem = new CompletionItem();
            completionItem.setInsertText("DESCRIPTION");
            completionItem.setLabel("DESCRIPTION");
            completionItem.setKind(CompletionItemKind.Snippet);
            completionItem.setDetail("Set the description of this test file.");
            completionItemList.add(completionItem);

            client.logMessage(new MessageParams(MessageType.Log, "Returning list"));
            return Either.forLeft(completionItemList);
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Open detected"));

        Parser parser = new Parser();
        client.logMessage(new MessageParams(MessageType.Log, "Parser created"));
        List<Statement> statementList = parser.parse(params.getTextDocument().getText());

        client.logMessage(new MessageParams(MessageType.Log, "Statement count: " + statementList.size()));
        for (Statement statement : statementList) {
            client.logMessage(new MessageParams(MessageType.Log,
                    "["
                            + statement.getKeyword()
                            + "] "
                            + statement.getValue()
            ));
        }
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Change detected"));
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Close detected"));
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Save detected"));
    }
}
