package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BuildTest {

    public static void main(String[] args) throws InterruptedException {
        /* build api sync **/
        Cache<Object, Object> cache1 = Caffeine.newBuilder().build();
        cache1.get(1, k -> null);

        LoadingCache<Integer, Integer> cache2 = Caffeine.newBuilder().build(new CacheLoader<Integer, Integer>() {
            @Override
            public @Nullable Integer load(@NonNull Integer key) throws Exception {
                // ForkJoinPool.commonPool-worker-9,load,-1
                System.out.println(Thread.currentThread().getName() + ",load," + key);
                if (key == Integer.MAX_VALUE) {
                    throw new RuntimeException("overflow");
                }
                return key > 0 ? key : null;
            }
        });
        System.out.println(cache2.getIfPresent(-1)); // null
        /* build api async **/

        AsyncCache<Object, Object> cache3 = Caffeine.newBuilder().buildAsync();
        CompletableFuture<Object> f3 = cache3.get(1, (k, v) -> CompletableFuture.supplyAsync(() -> null));

        AsyncLoadingCache<Object, Object> cache4 = Caffeine.newBuilder().buildAsync(new CacheLoader<Object, Object>() {
            @Override
            public @Nullable Object load(@NonNull Object key) throws Exception {
                return key;
            }
        });
        CompletableFuture<Object> f4 = cache4.get(1);

        AsyncLoadingCache<Object, Object> cache5 = Caffeine.newBuilder().buildAsync(new AsyncCacheLoader<Object, Object>() {
            @Override
            public @NonNull CompletableFuture<Object> asyncLoad(@NonNull Object key, @NonNull Executor executor) {
                return CompletableFuture.completedFuture(key);
            }
        });
        CompletableFuture<Object> f5 = cache5.get(1);
    }
}
