package org.nextlab.tobdd.cache;

import org.nextlab.tobdd.Node;

public interface Cache {

    /**
     * @param key is a binary BDD operation.
     * @return the result of the operation, if not in cache, compute (load) it.
     */
    Node getOrCompute(CacheKey key);
}
