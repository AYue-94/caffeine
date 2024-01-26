package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MetricsTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        LoadingCache<Object, Object> cache = Caffeine
            .newBuilder()
            .recordStats()
            .build(new CacheLoader<Object, Object>() {
                @Override
                public @Nullable Object load(@NonNull Object key) throws Exception {
                    return key;
                }
            });
        cache.get(1); // missCount++ loadSuccessCount++
        System.out.println(cache.stats());
        cache.get(1); // hitCount++
        System.out.println(cache.stats());
    }
}
