package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MaximumTest {
    public static void main(String[] args) throws InterruptedException, IOException {
//        Cache<Object, Object> cache = Caffeine.newBuilder().maximumSize(1).build(); // Cache = SSMS.class
//        cache.put(1, 100); // Node = PSMS.class
//        cache.put(2, 200);
//        System.out.println(cache.getIfPresent(1)); // 100
//        System.out.println(cache.getIfPresent(2)); // 200
//        System.out.println(cache.estimatedSize()); // 2
//        TimeUnit.MILLISECONDS.sleep(10); // wait evict
//        System.out.println(cache.getIfPresent(1)); // null
//        System.out.println(cache.getIfPresent(2)); // 200
//

        int max = 1000;
        // SSMS
        Cache<Object, Object> cache2 = Caffeine.newBuilder().maximumSize(max).build(); // Cache = SSMS.class
        for (int i = 0; i <= max / 2; i++) {
            cache2.put(i, i * 100); // 501次 开始初始化 频率采集 FrequencySketch
        }


        // SSMW
        Cache<Object, Object> cache3 = Caffeine.newBuilder().maximumWeight(max).weigher(new Weigher<Object, Object>() {
            @Override
            public @NonNegative int weigh(@NonNull Object key, @NonNull Object value) {
                return 1;
            }
        }).build();

        System.in.read();
    }
}
