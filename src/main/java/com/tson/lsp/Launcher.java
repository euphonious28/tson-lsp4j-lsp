package com.tson.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Entry point of the TSON LSP (using lsp4j). Developed with the following sources: <br/>
 * - https://medium.com/ballerina-techblog/implementing-a-language-server-how-hard-can-it-be-part-1-introduction-c915d2437076
 */
public class Launcher {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Turn off logger as we're using stdio
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(Level.OFF);

        // Call to start server
        startServer(System.in, System.out);
    }

    /**
     * Start language server
     */
    public static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        // Create server object
        LanguageServer languageServer = new LanguageServer();

        // Create connection to client
        org.eclipse.lsp4j.jsonrpc.Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(languageServer, in, out);

        // Retrieve the client
        LanguageClient languageClient = launcher.getRemoteProxy();

        // Store client in server object
        languageServer.connect(languageClient);

        // Start listening for messages
        Future<?> listener = launcher.startListening();
        listener.get();
    }
}
