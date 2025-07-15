package com.genia.app.vector;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class VectorInitializer {

    public static void init(LuceneVectorStore vectorStore) throws IOException {
        List<Document> documents = new ArrayList<>();
        List<Law> laws = loadLaws();
        for (Law law : laws) {
            Document doc = new Document(
                    law.getId(),
                    law.getContent(),
                    Map.of("title", law.getTitle())
            );
            documents.add(doc);
        }
        vectorStore.add(documents);
        log.debug("Loaded " + laws.size() + " laws into Lucene");
    }

    public static List<Law> loadLaws() {
        try (InputStream input = new ClassPathResource("laws.yaml").getInputStream()) {
            Yaml yaml = new Yaml();
            LawData data = yaml.loadAs(input, LawData.class);
            return data.getLaws();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load laws.yaml", e);
        }
    }

    @Data
    public static class Law {
        private String id;
        private String title;
        private String content;
    }

    @Data
    public static class LawData {
        private List<Law> laws;
    }
}
