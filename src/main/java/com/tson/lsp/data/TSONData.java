package com.tson.lsp.data;

import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.runner.TSONRunner;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TSONData {

    List<Keyword> keywordList = new ArrayList<>();

    public TSONData() {
        TSONRunner tsonRunner = new TSONRunner(Paths.get("").toAbsolutePath().toFile());
        keywordList.addAll(tsonRunner.getTsonInterpreter().getKeywords());
    }

    public List<Keyword> getKeywordList() {
        return keywordList;
    }
}
