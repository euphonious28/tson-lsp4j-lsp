package com.tson.parser;

public class Statement {
    private Keyword keyword;
    private String value;

    public Statement(String statement) {
        String[] args = statement.split(" ", 2);

        try {
            keyword = Keyword.fromCode(args[0]);
        } catch (IllegalArgumentException e) {
            keyword = null;
        }
        value = args[1];
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public String getValue() {
        return value;
    }
}
