package com.tson.lsp;

import com.euph28.tson.core.keyword.Keyword;
import com.tson.lsp.data.TSONData;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Text document service for handling LPS text document events
 */
public class TextDocumentService implements org.eclipse.lsp4j.services.TextDocumentService {

    /* ----- VARIABLES ------------------------------ */

    /**
     * Remote language client
     */
    private LanguageClient client;

    /**
     * Legend of semantic tokens
     */
    private final SemanticTokensLegend semanticTokensLegend;

    /**
     * TSON related data that is needed to provide LSP
     */
    final TSONData data = new TSONData();

    /**
     * Map of file-uri to file-content
     */
    // TODO: Change to a better object with version tracking
    Map<String, String> fileContentMap = new HashMap<>();

    /* ----- CONSTRUCTOR ------------------------------ */
    public TextDocumentService() {
        // Create Semantic Tokens Legend
        List<String> tokenTypeList = new ArrayList<>();
        tokenTypeList.add(SemanticTokenTypes.Function);
        semanticTokensLegend = new SemanticTokensLegend(tokenTypeList, new ArrayList<>());
    }

    /* ----- SETTER ------------------------------ */

    /**
     * Store the remote Language Client
     *
     * @param client Language Client this is providing service to
     */
    public void setClient(LanguageClient client) {
        this.client = client;
    }

    /* ----- GETTER ------------------------------ */

    /**
     * Get the Semantic Tokens legend
     */
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
        client.logMessage(new MessageParams(MessageType.Log, "Retrieving full semantics"));
        return CompletableFutures.computeAsync(cancelChecker -> {
            List<Integer> result = new ArrayList<>();
            String fileContent = fileContentMap.get(params.getTextDocument().getUri());

            // Manually open file if content doesn't exist
            if (fileContent == null) {
                try {
                    Path documentPath = Paths.get(new URL(params.getTextDocument().getUri()).toURI());
                    fileContent = Files.readString(documentPath);
                } catch (URISyntaxException | MalformedURLException e) {
                    client.logMessage(new MessageParams(MessageType.Error, "Invalid document path: " + params.getTextDocument().getUri()));
                    return new SemanticTokens(result);
                } catch (IOException e) {
                    client.logMessage(new MessageParams(MessageType.Error, "Failed to read document: " + params.getTextDocument().getUri()));
                    return new SemanticTokens(result);
                }
            }
            cancelChecker.checkCanceled();

            // Convert from String to List<String>
            List<String> document = List.of(fileContent.split("\r\n|\n|\r"));
            cancelChecker.checkCanceled();

            // Parse document for semantics
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

            // Transform into relative
            for (int i = (result.size() - 1) / 5; i > 0; i--) {     // Run backwards otherwise you'll update data you need
                int updatedLine = result.get(i * 5) - result.get((i - 1) * 5);
                int updatedStartChar = updatedLine == 0 ? result.get(i * 5 + 1) - result.get((i - 1) * 5 + 1) : result.get(i * 5 + 1);
                result.set(i * 5, updatedLine);
                result.set(i * 5 + 1, updatedStartChar);
            }

            client.logMessage(new MessageParams(MessageType.Log, "Returning full semantics"));
            return new SemanticTokens(result);
        });
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Open detected, file: " + params.getTextDocument().getUri()));
        // Retrieve existing item
        String existingItem = fileContentMap.get(params.getTextDocument().getUri());

        // Store item
        fileContentMap.put(
                params.getTextDocument().getUri(),
                existingItem
        );
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        client.logMessage(new MessageParams(MessageType.Log, "Change detected, file: " + params.getTextDocument().getUri()));
        // Report error if it isn't tracked
        if (!fileContentMap.containsKey(params.getTextDocument().getUri())) {
            client.logMessage(new MessageParams(MessageType.Error, "Change detected but file wasn't tracked. File: " + params.getTextDocument().getUri()));
            return;
        }

        // Otherwise, update existing item if version is correct
        params.getContentChanges().forEach(changeEvent -> fileContentMap.put(
                params.getTextDocument().getUri(),
                changeEvent.getText()
        ));
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
