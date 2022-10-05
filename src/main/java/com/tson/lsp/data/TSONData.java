package com.tson.lsp.data;

import com.euph28.tson.core.keyword.Keyword;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.euph28.tson.runner.TSONRunner;
import com.tson.lsp.utility.tson.DirectContentProvider;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TSONData {

    TSONRunner runner;
    List<Keyword> keywordList = new ArrayList<>();

    public TSONData() {
        runner = new TSONRunner(Paths.get("").toAbsolutePath().toFile());
        keywordList.addAll(runner.getTsonInterpreter().getKeywords());
        getTsonInterpreter().addContentProvider(new DirectContentProvider());
    }

    public TSONInterpreter getTsonInterpreter() {
        return runner.getTsonInterpreter();
    }

    public List<Keyword> getKeywordList() {
        return keywordList;
    }
}
