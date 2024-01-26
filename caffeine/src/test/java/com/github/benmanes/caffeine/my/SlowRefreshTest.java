package com.github.benmanes.caffeine.my;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SlowRefreshTest {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor es = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10), new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("cache-executor-" + counter.incrementAndGet());
                return thread;
            }
        }/*, new RejectedExecutionHandler() {
            final RejectedExecutionHandler delegate = new ThreadPoolExecutor.DiscardOldestPolicy();

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("rejected " + r.getClass());
                delegate.rejectedExecution(r, executor);
            }
        }*/);
        LoadingCache<Object, Object> cache = Caffeine.newBuilder()
//            .maximumSize(10000)
            .executor(es).refreshAfterWrite(Duration.ofSeconds(1)).build(new CacheLoader<Object, Object>() {
            @Override
            public @Nullable Object load(@NonNull Object key) throws Exception {
                System.out.println(Thread.currentThread().getName() + " load :" + key);
                Thread.sleep(30000);
                return key;
            }
        });


        for (int i = 0; i < 14; i++) {
            cache.put(i, i);

        }
        Thread.sleep(2000);
        for (int i = 0; i < 14; i++) {
            cache.getIfPresent(i);
        }

        cache.put(200, 20);
        System.out.println(1111);
    }
}
