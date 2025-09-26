package org.bot.rag;

import java.util.Random;

public class Embedding {

    private static DL4JEmbeddingGenerator dl4JEmbeddingGenerator = new DL4JEmbeddingGenerator(50000, 256);

    public static float[] dl4JEmbedding(
            String text
    ) {
        return dl4JEmbeddingGenerator.generateEmbedding(text);
    }

    // Упрощённая генерация эмбеддингов (в реальности используйте модель для эмбеддингов)
    public static float[] simpleEmbedding(String text) {
        // Заглушка - в реальном проекте используйте:
        // - GigaChat Embeddings API
        // - или модели типа sentence-transformers
        float[] embedding = new float[384]; // типичный размер для эмбеддингов
        Random random = new Random(text.hashCode());
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = random.nextFloat();
        }
        return embedding;
    }
}
