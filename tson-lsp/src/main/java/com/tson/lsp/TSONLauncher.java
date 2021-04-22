package com.tson.lsp;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TSONLauncher {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Turn off logger as we're using stdio
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(Level.OFF);

        // Start server
        startServer(System.in, System.out);
    }

    public static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {

        // Create server
        TSONTestLanguageServer languageServer = new TSONTestLanguageServer();

        // Create connection
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(languageServer, in, out);

        // Retrieve the client
        LanguageClient languageClient = launcher.getRemoteProxy();

        // Link server - client
        languageServer.connect(languageClient);

        // Start message listener
        Future<?> listener = launcher.startListening();
        listener.get();
    }
}
