package com.genia.app.vector;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LuceneVectorStore implements VectorStore {

    public static final String INDEX_DIRECTORY = "target/index-directory";

    EmbeddingModel embeddingModel;

    private final IndexWriter writer;
    private final IndexSearcher searcher;

    public LuceneVectorStore(EmbeddingModel embeddingModel) throws IOException {
        this.embeddingModel = embeddingModel;
        boolean emptyDirectory = createIndexDirectory();
        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig();
        this.writer = new IndexWriter(directory, config);
        this.searcher = new IndexSearcher(DirectoryReader.open(writer));

        if (emptyDirectory) {
            VectorInitializer.init(this);
            log.info("Lucene init successfully");
        }
    }

    private boolean createIndexDirectory() throws IOException {
        Path indexPath = Paths.get(INDEX_DIRECTORY);
        if (!Files.exists(indexPath)) {
            Files.createDirectories(indexPath);
            log.info("Created index directory: {}", indexPath.toAbsolutePath());
            return true;
        }
        log.info("Lucene folder found, no init required");
        return false;
    }

    @Override
    public void add(List<Document> documents) {
        try {
            for (Document doc : documents) {
                String id = doc.getId();
                String content = doc.getText();
                Map<String, Object> metadata = doc.getMetadata();
                float[] embedding = embeddingModel.embed(content);

                org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();
                luceneDoc.add(new StringField("id", id, Field.Store.YES));
                luceneDoc.add(new TextField("content", content, Field.Store.YES));
                luceneDoc.add(new TextField("title", metadata.get("title").toString(), Field.Store.YES));

                log.debug("Doc id: {}, title: {}", doc.getId(), doc.getMetadata().get("title"));

                luceneDoc.add(new KnnFloatVectorField("embedding", embedding));
                writer.addDocument(luceneDoc);
            }
            writer.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Document> similaritySearch(SearchRequest request) {
        float[] embedding = embeddingModel.embed(request.getQuery());
        int k = request.getTopK();

        try {
            Query query = new KnnVectorQuery("embedding", embedding, k);
            TopDocs topDocs = searcher.search(query, k);
            List<Document> result = new ArrayList<>();

            for (ScoreDoc sd : topDocs.scoreDocs) {
                org.apache.lucene.document.Document luceneDoc = searcher.doc(sd.doc);
                String id = luceneDoc.get("id");
                String content = luceneDoc.get("content");
                String title = luceneDoc.get("title");
                result.add(new Document(id, content, Map.of("title", title)));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(List<String> idList) {
        try {
            for (String id : idList) {
                Term term = new Term("id", id);
                writer.deleteDocuments(term);
            }
            writer.commit();
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete documents by ID", e);
        }
    }

    @Override
    public void delete(Filter.Expression filterExpression) {
        try {
            Query query = convertFilterToLuceneQuery(filterExpression);
            writer.deleteDocuments(query);
            writer.commit();
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete documents by filter", e);
        }
    }

    private Query convertFilterToLuceneQuery(Filter.Expression expr) {
        switch (expr.type()) {
            case EQ -> {
                String fieldName = ((Filter.Key) expr.left()).key();
                String value = (String) ((Filter.Value) expr.right()).value();
                return new TermQuery(new Term(fieldName, value));
            }
        }
        throw new UnsupportedOperationException("Filter expression not supported: " + expr);
    }
}