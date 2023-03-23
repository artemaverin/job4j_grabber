package ru.job4j.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCache<K, V> {
    private final Map<K, SoftReference<V>> cache = new HashMap<>();

    public Map<K, SoftReference<V>> getCache() {
        return cache;
    }

    public final void put(K key, V value) {
        cache.put(key, new SoftReference<>(value));
    }

    public final V get(K key) {
        V res = null;
        if (!getCache().containsKey(key)) {
            res = load(key);
        } else {
            res = cache.get(key).get();
        }
        return res;
    }

    protected abstract V load(K key);
}
