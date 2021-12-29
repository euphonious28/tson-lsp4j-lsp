package com.tson.lsp;

import com.euph28.tson.core.keyword.Keyword;
import com.tson.lsp.data.TSONData;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TSONTestTextDocumentService implements TextDocumentService {

    /* ----- VARIABLES ------------------------------ */

    private LanguageClient client;

    private SemanticTokensLegend semanticTokensLegend;

    private TSONData data = new TSONData();

    /* ----- CONSTRUCTOR ------------------------------ */
    public TSONTestTextDocumentService() {
        List<String> tokenTypeList = new ArrayList<>();
        tokenTypeList.add(SemanticTokenTypes.Function);
        semanticTokensLegend = new SemanticTokensLegend(tokenTypeList, new ArrayList<>());
    }

    /* ----- SETTER ------------------------------ */

    public void setClient(LanguageClient client) {
        this.client = client;
    }

    /* ----- GETTER ------------------------------ */

    public SemanticTokensLegend getSemanticTokensLegend() {
        return semanticTokensLegend;
    }

    /* ----- OVERRIDE: TextDocumentService ------------------------------ */

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {

        // TODO: Implement a proper autocomplete using the (CompletionParams)position
        client.logMessage(new MessageParams(MessageType.Log, "Calculating completion"));
        return CompletableFuture.supplyAsync(() -> {
            // List of completion items to return
            List<CompletionItem> completionItemList = new ArrayList<>();

            String keywordList = "";
            for (Keyword keyword : data.getKeywordList()) {
                CompletionItem completionItem = new CompletionItem();
                completionItem.setInsertText(keyword.getCode());
                completionItem.setLabel(keyword.getCode());
                completionItem.setFilterText(keyword.getCode() + " " + String.join(" ", keyword.getLspTags()));
                completionItem.setDetail(keyword.getLspDescriptionShort());
                completionItem.setDocumentation(keyword.getLspDescriptionLong());
                completionItem.setKind(CompletionItemKind.Function);
                completionItemList.add(completionItem);
                keywordList = keywordList + "|" + keyword.getCode();
            }
            client.logMessage(new MessageParams(MessageType.Log, keywordList));

            client.logMessage(new MessageParams(MessageType.Log, "Returning list"));
            return Either.forLeft(completionItemList);
        });
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Retrieving semantics"));
        return CompletableFuture.supplyAsync(() -> {
            // Open and read file
            Path documentPath = Paths.get("");
            try {
                documentPath = Paths.get(new URL(params.getTextDocument().getUri()).toURI());
            } catch (URISyntaxException | MalformedURLException e) {
                client.logMessage(new MessageParams(MessageType.Error, "Invalid document: " + params.getTextDocument().getUri()));
            }
            List<String> document = new ArrayList<>();
            try {
                document = Files.readAllLines(documentPath);
            } catch (IOException e) {
                client.logMessage(new MessageParams(MessageType.Error, "Failed to read document: " + params.getTextDocument().getUri()));
            }

            // Parse document for semantics
            List<Integer> result = new ArrayList<>();
            for (int lineIndex = 0; lineIndex < document.size(); lineIndex++) {
                // For each line:
                String line = document.get(lineIndex);

                // Check if it contains a keyword
                for (Keyword keyword : data.getKeywordList()) {
                    // If it contains, get the index of it
                    // TODO: Detect multiple keywords per line
                    if (line.contains(keyword.getCode())) {
                        // Insert result based on specification: https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textDocument_semanticTokens
                        result.add(lineIndex);                          // Line
                        result.add(line.indexOf(keyword.getCode()));    // StartChar
                        result.add(keyword.getCode().length());         // Length
                        result.add(0);                                  // TokenType | TODO: Handle different types
                        result.add(0);                                  // TokenModifier | TODO: Handle modifiers
                    }
                }
            }

            // Logging of current data
            StringBuilder log = new StringBuilder();
            for (int i = 0; i < result.size() / 5; i++) {
                log.append(String.format(
                        "[%d, %d, %d, %d, %d]\n",
                        result.get(i * 5),
                        result.get(i * 5 + 1),
                        result.get(i * 5 + 2),
                        result.get(i * 5 + 3),
                        result.get(i * 5 + 4)
                ));
            }
            client.logMessage(new MessageParams(MessageType.Info, "Returning tokens: " + log.toString()));

            // Transform into relative
            for (int i = (result.size() - 1) / 5; i > 0; i--) {     // Run backwards otherwise you'll update data you need
                int updatedLine = result.get(i * 5) - result.get((i - 1) * 5);
                int updatedStartChar = updatedLine == 0 ? result.get(i * 5 + 1) - result.get((i - 1) * 5 + 1) : result.get(i * 5 + 1);
                result.set(i * 5, updatedLine);
                result.set(i * 5 + 1, updatedStartChar);
            }

            // Logging of current data
            log = new StringBuilder();
            for (int i = 0; i < result.size() / 5; i++) {
                log.append(String.format(
                        "[%d, %d, %d, %d, %d]\n",
                        result.get(i * 5),
                        result.get(i * 5 + 1),
                        result.get(i * 5 + 2),
                        result.get(i * 5 + 3),
                        result.get(i * 5 + 4)
                ));
            }
            client.logMessage(new MessageParams(MessageType.Info, "Returning tokens: " + log.toString()));

            return new SemanticTokens(result);
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Open detected"));
//
//        Parser parser = new Parser();
//        client.logMessage(new MessageParams(MessageType.Log, "Parser created"));
//        List<Statement> statementList = parser.parse(params.getTextDocument().getText());
//
//        client.logMessage(new MessageParams(MessageType.Log, "Statement count: " + statementList.size()));
//        for (Statement statement : statementList) {
//            client.logMessage(new MessageParams(MessageType.Log,
//                    "["
//                            + statement.getKeyword()
//                            + "] "
//                            + statement.getValue()
//            ));
//        }
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
