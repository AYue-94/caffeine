package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import java.time.Duration;
import java.util.concurrent.Executors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RemovalListenerTest {
    public static void main(String[] args) {

        Cache<Object, Object> cache = Caffeine.newBuilder()
            .maximumSize(1)
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {
                    System.out.println(Thread.currentThread().getName() + " remove key = " + key + ", cause = " + cause);
                }
            })
            .evictionListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(@Nullable Object key, @Nullable Object value, @NonNull RemovalCause cause) {
                    System.out.println(Thread.currentThread().getName() + " evict key = " + key + ", cause = " + cause);
                }
            })
            .build();
    }
}
