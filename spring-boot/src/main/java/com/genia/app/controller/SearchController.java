package com.genia.app.controller;

import com.genia.app.vector.LuceneVectorStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private LuceneVectorStore vectorStore;

    @PostMapping
    public List<Document> search(@RequestBody String query) {
        log.debug("Received search query: " + query);
        return vectorStore.similaritySearch(SearchRequest.builder().query(query).build());
    }
}
