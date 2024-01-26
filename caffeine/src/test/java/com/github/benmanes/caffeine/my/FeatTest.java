package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.github.benmanes.caffeine.cache.Ticker;
import com.github.benmanes.caffeine.cache.Weigher;
import java.time.Duration;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FeatTest {
    public static void main(String[] args) {
        LoadingCache<Object, Object> cache = Caffeine.newBuilder()

            // removal
            // RemovalCause - SIZE (evict)
            .maximumSize(1000)
            .maximumWeight(1000)
            .weigher(new Weigher<Object, Object>() {
                @Override
                public @NonNegative int weigh(@NonNull Object key, @NonNull Object value) {
                    return 1;
                }
            })
            // RemovalCause - EXPIRED (expire)
            .expireAfterWrite(Duration.ofHours(1))
            .expireAfterAccess(Duration.ofHours(1))
            .expireAfter(new Expiry<Object, Object>() {
                @Override
                public long expireAfterCreate(@NonNull Object key, @NonNull Object value, long currentTime) {
                    return 0;
                }

                @Override
                public long expireAfterUpdate(@NonNull Object key, @NonNull Object value, long currentTime,
                    @NonNegative long currentDuration) {
                    return 0;
                }

                @Override
                public long expireAfterRead(@NonNull Object key, @NonNull Object value, long currentTime,
                    @NonNegative long currentDuration) {
                    return 0;
                }
            })
            // RemovalCause - COLLECTED
            .softValues()
            .weakKeys()
            .weakValues()

            // refresh
            .refreshAfterWrite(Duration.ofHours(1))

            // observation
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {

                }
            })
            .evictionListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {

                }
            })
            .recordStats()
            .ticker(Ticker.systemTicker())

            // threads
            .executor(ForkJoinPool.commonPool())
            .scheduler(Scheduler.disabledScheduler())

            // LoadingCache
            .build(new CacheLoader<Object, Object>() {
                @Override
                public @Nullable Object load(@NonNull Object key) throws Exception {
                    return null;
                }
            });

        // get cache miss -> do nothing
        cache.getIfPresent(1);
        // get cache miss -> cache loader
        cache.get(1);

        cache.get(1, new Function<Object, Object>() {
            @Override
            public Object apply(Object key) {
                return null;
            }
        });
        // cache loader reload / load
        cache.refresh(1);
    }
}
