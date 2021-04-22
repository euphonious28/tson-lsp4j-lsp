package com.tson.parser;

public enum Keyword {

    /* ----- VALUES ------------------------------ */

    COMMENT("#", "#", "#"),
    ID("ID", "ID", "ID"),
    TITLE("TITLE", "TITLE", "TITLE"),
    DESCRIPTION("DESCRIPTION", "DESCRIPTION", "DESCRIPTION"),
    TEST("TEST", "TEST", "TEST"),
    STEP("STEP", "STEP", "STEP"),
    ASSERT("ASSERT", "ASSERT", "ASSERT"),
    EQUAL("EQUAL", "EQUAL", "EQUAL");

    /* ----- STATIC METHODS ------------------------------ */
    public static Keyword fromCode(String code) {
        for (Keyword keyword : Keyword.values()) {
            if (keyword.code.equals(code)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("No constant with text " + code + " found");
    }

    /* ----- CONSTRUCTOR ------------------------------ */

    Keyword(String code, String shortDescription, String longDescription) {
        this.code = code;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    /* ----- VARIABLES ------------------------------ */
    private String code;
    private String shortDescription;
    private String longDescription;

    /* ----- GETTERS ------------------------------ */
    public String getCode() {
        return code;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }


}
