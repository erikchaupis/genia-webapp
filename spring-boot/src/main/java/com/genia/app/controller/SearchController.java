package com.genia.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private VectorStore vectorStore;

    @PostMapping
    public List<Document> search(@RequestBody String query) {
        log.debug("Received search query: " + query);
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).build());
    }
}
