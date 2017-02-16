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

    /**
     * Wrap an existing map with a name and an empty prefix.
     * @param name an arbitrary map name
     * @param map an existing map that will be wrapped
     */
    public NamedMap(String name, Map<K, V> map) {
        this(DEFAULT_PREFIX, name, map);
    }

    /**
     * Wrap an existing map with arbitrary name and a prefix.
     * @param prefix an arbitrary prefix that describes a map
     * @param name an arbitrary name that is attached to the map
     * @param map map that will be wrapped
     */
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

    /**
     * Return the prefix attached to the map
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Return the name attached to the map
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the map instance that is wrapped by this object
     * @return a wrapped map
     */
    public Map<K, V> getDelegate() {
        return delegate;
    }
}
