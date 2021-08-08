/*
 * Copyright 2015 Ben Manes. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.benmanes.caffeine.cache.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.testing.ConcurrentMapTestSuiteBuilder;
import com.google.common.collect.testing.Helpers;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.TestMapGenerator;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;

import junit.framework.TestSuite;

/**
 * A JUnit test suite factory for the map tests from Guava's testlib.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class MapTestFactory {

  private MapTestFactory() {}

  /** Returns a test suite based on the supplied configuration. */
  public static Stream<TestSuite> makeTests(CacheContext context) {
    var name = context.toString();
    var tests = new ArrayList<TestSuite>(2);
    tests.add(newTestSuite("Cache[" + name + "]", context, synchronousGenerator(context)));
    if (context.isAsync()) {
      tests.add(newTestSuite("AsyncCache[" + name + "]", context, asynchronousGenerator(context)));
    }
    return tests.stream();
  }

  /** Returns a test suite. */
  private static TestSuite newTestSuite(String name,
      CacheContext context, TestMapGenerator<?, ?> generator) {
    return ConcurrentMapTestSuiteBuilder
        .using(generator)
        .named(name)
        .withFeatures(
            MapFeature.GENERAL_PURPOSE,
            MapFeature.ALLOWS_NULL_ENTRY_QUERIES,
            CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
            CollectionSize.ANY)
        .withTearDown(() -> {
          context.cache = null;
          context.asyncCache = null;
        }).createTestSuite();
  }

  /** Returns a map generator for synchronous values. */
  private static TestStringMapGenerator synchronousGenerator(CacheContext context) {
    return new TestStringMapGenerator() {
      @Override protected Map<String, String> create(Map.Entry<String, String>[] entries) {
        if (context.cache() == null) {
          CacheGenerator.initialize(context);
        }

        @SuppressWarnings("unchecked")
        var cache = (Cache<String, String>) context.cache();
        cache.invalidateAll();
        for (var entry : entries) {
          cache.put(entry.getKey(), entry.getValue());
        }
        return cache.asMap();
      }
    };
  }

  /** Returns a map generator for asynchronous values. */
  private static TestAsyncMapGenerator asynchronousGenerator(CacheContext context) {
    return new TestAsyncMapGenerator() {
      @Override protected Map<String, CompletableFuture<String>> create(
          Map.Entry<String, CompletableFuture<String>>[] entries) {
        if (context.asyncCache() == null) {
          CacheGenerator.initialize(context);
        }

        @SuppressWarnings("unchecked")
        var cache = (AsyncCache<String, String>) context.asyncCache();
        cache.synchronous().invalidateAll();
        for (var entry : entries) {
          cache.put(entry.getKey(), entry.getValue());
        }
        return cache.asMap();
      }
    };
  }

  private static abstract class TestAsyncMapGenerator
      implements TestMapGenerator<String, CompletableFuture<String>> {

    static final CompletableFuture<String> JAN = CompletableFuture.completedFuture("January");
    static final CompletableFuture<String> FEB = CompletableFuture.completedFuture("February");
    static final CompletableFuture<String> MARCH = CompletableFuture.completedFuture("March");
    static final CompletableFuture<String> APRIL = CompletableFuture.completedFuture("April");
    static final CompletableFuture<String> MAY = CompletableFuture.completedFuture("May");

    @Override
    public SampleElements<Map.Entry<String, CompletableFuture<String>>> samples() {
      return new SampleElements<>(
          Helpers.mapEntry("one", JAN),
          Helpers.mapEntry("two", FEB),
          Helpers.mapEntry("three", MARCH),
          Helpers.mapEntry("four", APRIL),
          Helpers.mapEntry("five", MAY));
    }

    @Override
    public Map<String, CompletableFuture<String>> create(Object... entries) {
      @SuppressWarnings({"rawtypes", "unchecked"})
      Map.Entry<String, CompletableFuture<String>>[] array = new Map.Entry[entries.length];
      int i = 0;
      for (Object o : entries) {
        @SuppressWarnings("unchecked")
        Map.Entry<String, CompletableFuture<String>> e =
            (Map.Entry<String, CompletableFuture<String>>) o;
        array[i++] = e;
      }
      return create(array);
    }

    protected abstract Map<String, CompletableFuture<String>> create(
        Map.Entry<String, CompletableFuture<String>>[] entries);

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public final Map.Entry<String, CompletableFuture<String>>[] createArray(int length) {
      return new Map.Entry[length];
    }

    @Override
    public final String[] createKeyArray(int length) {
      return new String[length];
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override public final CompletableFuture<String>[] createValueArray(int length) {
      return new CompletableFuture[length];
    }

    @Override
    public Iterable<Map.Entry<String, CompletableFuture<String>>> order(
        List<Map.Entry<String, CompletableFuture<String>>> insertionOrder) {
      return insertionOrder;
    }
  }
}
