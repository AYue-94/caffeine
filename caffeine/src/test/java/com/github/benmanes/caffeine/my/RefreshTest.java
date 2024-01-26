package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class RefreshTest {
    public static void main(String[] args) throws InterruptedException {
        LoadingCache<Integer, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10)) // 10s后过期
            .refreshAfterWrite(Duration.ofNanos(1)) // 写后
            .build(new CacheLoader<Integer, String>() {
                @Override
                public @Nullable String load(@NonNull Integer key) throws Exception {
                    String value = UUID.randomUUID().toString();
                    System.out.println(Thread.currentThread().getName() + ",load," + key + "," + value);
                    return value;
                }
            });
        cache.put(1, "1"); // 缓存存在，且未过期
        TimeUnit.SECONDS.sleep(1);
        System.out.println(cache.get(1)); // 判断超过refreshAfterWrite，异步刷新
        TimeUnit.SECONDS.sleep(1);

    }
}
