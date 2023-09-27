package org.nextlab.tobdd.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.nextlab.tobdd.Node;

public class LockFreeCache implements Cache {
    private final LoadingCache<CacheKey, Node> _cache;

    public LockFreeCache(int cacheSize, CacheLoader<CacheKey, Node> loader) {
        this._cache = Caffeine.newBuilder()
                .maximumSize(cacheSize)
                .build(loader);
    }

    public Node getOrCompute(CacheKey key) {
        return _cache.get(key);
    }
}
