package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PolicyTest {
    public static void main(String[] args) {
        LoadingCache<Object, Object> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofSeconds(30))
            .executor(Executors.newFixedThreadPool(4))
            .build(new CacheLoader<Object, Object>() {
                @Override
                public @Nullable Object load(@NonNull Object key) throws Exception {
                    return key;
                }
            });

        Policy<Object, Object> policy = cache.policy();

        // expireAfterWrite
        Optional<Policy.Expiration<Object, Object>> expiration = policy.expireAfterWrite();
        if (expiration.isPresent()) {
            // 运行时修改阈值
            System.out.println(expiration.get().getExpiresAfter());
            expiration.get().setExpiresAfter(Duration.ofSeconds(60));
            // old/young topK
            expiration.get().oldest(10);
            expiration.get().youngest(10);
            // get key ttl
            expiration.get().ageOf(1).ifPresent(new Consumer<Duration>() {
                @Override
                public void accept(Duration duration) {
                    System.out.println(duration);
                }
            });
        }

        // 容量
        Optional<Policy.Eviction<Object, Object>> eviction = policy.eviction();
        if (eviction.isPresent()) {
            // 运行时修改阈值
            System.out.println(eviction.get().getMaximum());
            eviction.get().setMaximum(5000);
            // cold/hot top k
            eviction.get().coldest(10);
            eviction.get().hottest(10);
        }
    }
}
