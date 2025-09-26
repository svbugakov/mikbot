package org.bot.rag;

import java.util.Random;

public class DL4JEmbeddingGenerator {
    private final int vocabSize;
    private final int embeddingSize;
    private final float[][] wordEmbeddings;

    public DL4JEmbeddingGenerator(int vocabSize, int embeddingSize) {
        this.vocabSize = vocabSize;
        this.embeddingSize = embeddingSize;
        this.wordEmbeddings = new float[vocabSize][embeddingSize];
        initializeEmbeddings();
    }

    private void initializeEmbeddings() {
        Random random = new Random(42); // Фиксированный seed для воспроизводимости

        for (int i = 0; i < vocabSize; i++) {
            for (int j = 0; j < embeddingSize; j++) {
                // Более разумная инициализация
                wordEmbeddings[i][j] = (random.nextFloat() - 0.5f) * 2f;
            }
            // Нормализуем каждый word embedding
            normalizeWordEmbedding(i);
        }
    }

    private void normalizeWordEmbedding(int wordIndex) {
        float magnitude = 0.0f;
        for (float v : wordEmbeddings[wordIndex]) {
            magnitude += v * v;
        }
        magnitude = (float) Math.sqrt(magnitude);

        for (int i = 0; i < embeddingSize; i++) {
            wordEmbeddings[wordIndex][i] /= magnitude;
        }
    }

    public float[] generateEmbedding(String text) {
        float[] embedding = new float[embeddingSize];
        String[] words = preprocessText(text);
        int wordCount = 0;

        for (String word : words) {
            int wordIndex = Math.abs(word.hashCode()) % vocabSize;
            for (int i = 0; i < embeddingSize; i++) {
                embedding[i] += wordEmbeddings[wordIndex][i];
            }
            wordCount++;
        }

        if (wordCount > 0) {
            for (int i = 0; i < embeddingSize; i++) {
                embedding[i] /= wordCount;
            }
        }

        return normalizeVector(embedding);
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


    private String[] preprocessText(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-яёa-z\\s]", "")
                .split("\\s+");
    }
}