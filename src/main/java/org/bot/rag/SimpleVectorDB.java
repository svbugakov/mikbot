package org.bot.rag;

import chat.giga.client.GigaChatClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleVectorDB {
    private Map<String, float[]> vectors = new HashMap<>();
    private Map<String, String> texts = new HashMap<>();
    private GigaChatClient client;

    public SimpleVectorDB() {
    }

    public void setClient(GigaChatClient client) {
        this.client = client;
    }

    public void addDocument(String documentId, String text) {
        // Генерация эмбеддинга для документа (упрощённо)
        float[] embedding = Embedding.dl4JEmbedding(text);
        texts.put(documentId, text);
        vectors.put(documentId, embedding);
    }

    public List<String> findSimilar(float[] queryVector, int topK) {
        // Нормализуем query vector
        float[] normalizedQuery = normalizeVector(queryVector);

        List<Map.Entry<String, Double>> similarities = new ArrayList<>();

        for (Map.Entry<String, float[]> entry : vectors.entrySet()) {
            // Нормализуем каждый вектор базы
            float[] normalizedDoc = normalizeVector(entry.getValue());
            double similarity = cosineSimilarity(normalizedQuery, normalizedDoc);
            similarities.add(Map.entry(entry.getKey(), similarity));
        }

        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, similarities.size()); i++) {
            String docId = similarities.get(i).getKey();
            if (similarities.get(i).getValue() > 0.25) {
                result.add(texts.get(docId));
            }
        }

        return result;
    }

    private float[] normalizeVector(float[] vector) {
        float magnitude = 0.0f;
        for (float v : vector) {
            magnitude += v * v;
        }
        magnitude = (float) Math.sqrt(magnitude);

        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = vector[i] / magnitude;
        }
        return normalized;
    }


    private double cosineSimilarity(float[] a, float[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += Math.pow(a[i], 2);
            normB += Math.pow(b[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

}
