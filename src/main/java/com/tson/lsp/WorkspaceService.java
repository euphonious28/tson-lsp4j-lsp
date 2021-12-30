package com.tson.lsp;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;

public class WorkspaceService implements org.eclipse.lsp4j.services.WorkspaceService {

    /* ----- OVERRIDE: WorkspaceService ------------------------------ */

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {

    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {

    }
}
