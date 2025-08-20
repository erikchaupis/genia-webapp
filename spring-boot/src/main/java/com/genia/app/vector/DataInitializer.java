package com.genia.app.vector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DataInitializer {

    @Autowired
    VectorStore vectorStore;

    // Uncomment once to initialze the data on ChromaDB
    // Set spring.ai.vectorstore.chroma.initialize-schema=true in application.properties
    //@javax.annotation.PostConstruct
    public void init() {
        read("vector/contract.txt");
        read("vector/laws.txt");
    }

    private void read(String file) {
        TextReader textReader = new TextReader(new ClassPathResource(file));
        TokenTextSplitter splitter = new TokenTextSplitter(100, 100, 5, 10000, true);
        List<Document> documents = splitter.split(textReader.get());
        vectorStore.add(documents);
    }
}
