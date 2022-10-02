package com.tson.lsp;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Entry point of the TSON LSP (using lsp4j). Developed with the following sources: <br/>
 * - https://medium.com/ballerina-techblog/implementing-a-language-server-how-hard-can-it-be-part-1-introduction-c915d2437076
 * - https://github.com/adamvoss/vscode-languageserver-java-example
 */
public class Launcher {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        String port = args[0];
        try {
            Socket socket = new Socket("localhost", Integer.parseInt(port));

            startServer(socket);
        } catch (ExecutionException | InterruptedException| IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start language server
     */
    public static void startServer(Socket socket) throws ExecutionException, InterruptedException, IOException {
        // Create server object
        LanguageServer languageServer = new LanguageServer();

        // Create connection to client
        org.eclipse.lsp4j.jsonrpc.Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(
                languageServer,
                socket.getInputStream(),
                socket.getOutputStream()
        );

        // Retrieve the client
        LanguageClient languageClient = launcher.getRemoteProxy();

        // Store client in server object
        languageServer.connect(languageClient);

        // Start listening for messages
        Future<?> listener = launcher.startListening();
        listener.get();
    }
}
