package com.tson.lsp.data;

import com.euph28.tson.assertionengine.TSONAssertionEngine;
import com.euph28.tson.context.TSONContext;
import com.euph28.tson.filereader.FileReader;
import com.euph28.tson.interpreter.TSONInterpreter;
import com.euph28.tson.interpreter.keyword.Keyword;
import com.euph28.tson.restclientinterface.TSONRestClient;

import java.util.ArrayList;
import java.util.List;

public class TSONData {

    List<Keyword> keywordList = new ArrayList<>();

    public TSONData() {
        TSONInterpreter tsonInterpreter = new TSONInterpreter();
        TSONAssertionEngine tsonAssertionEngine = new TSONAssertionEngine();
        TSONRestClient tsonRestClient = new TSONRestClient(new FileReader("/"));
        TSONContext tsonContext = new TSONContext(tsonInterpreter, tsonRestClient);
        keywordList.addAll(tsonAssertionEngine.getKeywordList());
        keywordList.addAll(tsonRestClient.getKeywordList());
        keywordList.addAll(tsonContext.getKeywordList());
    }

    public List<Keyword> getKeywordList() {
        return keywordList;
    }
}
