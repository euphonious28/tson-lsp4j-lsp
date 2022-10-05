package com.tson.lsp.utility.semantictoken;

import java.util.ArrayList;
import java.util.List;

/**
 * Individual entry for a semantic token highlighting
 */
public class SemanticTokenEntry implements Comparable<SemanticTokenEntry> {
    /* ----- VARIABLES ------------------------------ */
    int lineIndex, startChar, tokenLength;
    String tokenType, tokenModifier;

    /* ----- CONSTRUCTOR ------------------------------ */
    public SemanticTokenEntry(int lineIndex, int startChar, int tokenLength, String tokenType, String tokenModifier) {
        this.lineIndex = lineIndex;
        this.startChar = startChar;
        this.tokenLength = tokenLength;
        this.tokenType = tokenType;
        this.tokenModifier = tokenModifier;
    }

    /* ----- METHODS ------------------------------ */

    /**
     * Update the contents of this entry to be relative to the provided entry
     *
     * @param targetEntry Target entry to be the base to relativize to
     */
    public void relativize(SemanticTokenEntry targetEntry) {
        lineIndex = lineIndex - targetEntry.getLineIndex();                                  // Update line to be relative
        startChar = lineIndex == 0 ? startChar - targetEntry.getStartChar() : startChar;     // Update char if same line
    }

    /* ----- GETTER ------------------------------ */
    public int getLineIndex() {
        return lineIndex;
    }

    public int getStartChar() {
        return startChar;
    }

    /**
     * Retrieve entry in the form of int list as per specification: https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textDocument_semanticTokens
     *
     * @return Entry contents in the form of an int list
     */
    public List<Integer> getAsIntList(List<String> tokenTypeList, List<String> tokenModifierList) {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(lineIndex);
        integerList.add(startChar);
        integerList.add(tokenLength);
        integerList.add(tokenTypeList.indexOf(tokenType));
        integerList.add(0);
        return integerList;
    }

    /* ----- OVERRIDE: COMPARATOR ------------------------------ */

    @Override
    public int compareTo(SemanticTokenEntry o) {
        if(this.getLineIndex() != o.getLineIndex()) {
            return this.getLineIndex() > o.getLineIndex() ? 1 : -1;
        }
        else if (this.getStartChar() != o.getStartChar()) {
            return this.getStartChar() > o.getStartChar() ? 1 : -1;
        } else {
            return 0;
        }
    }

}
