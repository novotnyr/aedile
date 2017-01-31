package com.github.novotnyr.aedile.util;

import java.util.Map;

/**
 * Represents a map that has associated name and optional name prefix.
 * @param <K> data type of keys
 * @param <V> data type of values
 */
public class NamedMap<K, V> {
    private static final String DEFAULT_PREFIX = "";

    private String prefix;

    private String name;

    private Map<K, V> delegate;

    public NamedMap(String name, Map<K, V> map) {
        this(DEFAULT_PREFIX, name, map);
    }

    public NamedMap(String prefix, String name, Map<K, V> map) {
        if(name == null) {
            throw new IllegalArgumentException("Name must be set");
        }
        if(map == null) {
            throw new IllegalArgumentException("Map must be set");
        }

        this.prefix = prefix;
        this.name = name;
        this.delegate = map;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public Map<K, V> getDelegate() {
        return delegate;
    }
}
