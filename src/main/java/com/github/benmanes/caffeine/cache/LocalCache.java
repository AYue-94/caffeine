/*
 * Copyright 2014 Ben Manes. All Rights Reserved.
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
package com.github.benmanes.caffeine.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * @author ben.manes@gmail.com (Ben Manes)
 */
interface LocalCache<K, V> extends ConcurrentMap<K, V> {

  long mappingCount();

  @Nullable V getIfPresent(Object key);

  V get(K key, Function<? super K, ? extends V> mappingFunction);

  Map<K, V> getAllPresent(Iterable<?> keys);

  void invalidateAll();

  void invalidateAll(Iterable<?> keys);

  void cleanUp();

  Iterator<K> keyIterator();
  Spliterator<K> keySpliterator();

  Iterator<V> valueIterator();
  Spliterator<V> valueSpliterator();

  Iterator<Entry<K, V>> entryIterator();
  Spliterator<Entry<K, V>> entrySpliterator();
}
