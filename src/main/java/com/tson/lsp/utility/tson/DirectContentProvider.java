package com.tson.lsp.utility.tson;

import com.euph28.tson.core.provider.ContentProvider;

/**
 * Direct provider that returns source as content
 */
public class DirectContentProvider implements ContentProvider {
    @Override
    public String getContent(String sourceName) {
        return sourceName;
    }
}
