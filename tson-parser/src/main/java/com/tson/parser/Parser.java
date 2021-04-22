package com.tson.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    /**
     * Converts a .psltest into a {@link List} of {@link Statement}
     *
     * @param code Code found within the .psltest script
     * @return List of {@link Statement} parsed from the {@code code}
     */
    public List<Statement> parse(String code) {

        /*
         * PARSER FLOW
         *
         * 1. Setup for splitting (step 2): Generate regex from Keywords
         * 2. Split from one long String into a list of Strings,
         *    each String starting with a Keyword (Statement String)
         * 3. Convert from String to Statement
         */

        /* 1. Setup for splitting (step 2): Generate regex from Keywords */
        String[] keywords = Arrays
                .stream(Keyword.values())
                .map(Keyword::getCode)
                .toArray(String[]::new);
        String regex = "(?=\\b(" + String.join("|", keywords) + ")\\b)";

        /* 2. Split from one long String into a list of Strings,
         *    each String starting with a Keyword (Statement String)
         */
        List<String> statementStringList = Arrays.asList(code.split(regex));

        /* 3. Convert from String to Statement */
        List<Statement> statementList = statementStringList
                .stream()
                .map(s -> s                                             // Clear redundant whitespaces
                    .trim()                                                 // Front and end
                        .replaceAll("[\\t\\n\\r]+"," ")   // Newline
                        .replaceAll("  +", " ")           // Double spaces
                )
                .map(Statement::new)                                    // Convert from String->Statement
                .filter(statement -> statement.getKeyword() != null)    // Remove if keyword mapping failed (first line)
                .collect(Collectors.toList());

        return statementList;
    }
}
