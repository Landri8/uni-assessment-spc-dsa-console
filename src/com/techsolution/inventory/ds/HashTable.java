package com.techsolution.inventory.ds;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple generic hash table implementation using separate chaining for collision handling.
 * Keys are hashed to an index to determine which bucket (linked list) stores the key-value pair.
 * Provides average-case O(1) time for insert, lookup, and removal, assuming a good hash distribution.
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class HashTable<K, V> {
    /**
     * Represents a key-value pair stored in a bucket.
     */
    private static class Node<K, V> {
        K key;
        V val;

        Node(K key, V val) {
            this.key = key;
            this.val = val;
        }
    }

    // Array of buckets; each bucket is a linked list of Node<K, V>
    @SuppressWarnings("unchecked")
    private final List<Node<K, V>>[] buckets;
    // Number of key-value pairs currently stored in the hash table
    private int size;

    /**
     * Constructs a new HashTable with the specified initial bucket capacity.
     * @param capacity number of buckets (should be a positive integer)
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        buckets = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new LinkedList<>();
        }
        size = 0;
    }

    /**
     * Computes the bucket index for a given key by hashing the key and taking modulus by bucket count.
     * @param key the key to hash
     * @return index of the bucket where this key should reside
     */
    private int hash(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    /**
     * Inserts or updates a key-value pair in the hash table.
     * If the key already exists, its associated value is overwritten.
     * @param key the key to insert or update
     * @param value the value to associate with the key
     */
    public void put(K key, V value) {
        int index = hash(key);
        List<Node<K, V>> bucket = buckets[index];
        // Check if the key already exists in this bucket
        for (Node<K, V> node : bucket) {
            if (node.key.equals(key)) {
                // Overwrite the existing value
                node.val = value;
                return;
            }
        }
        // Key not found: add new node to this bucket
        bucket.add(new Node<>(key, value));
        size++;
    }

    /**
     * Retrieves the value associated with the given key, or null if the key is not present.
     * @param key the key to look up
     * @return the value associated with the key, or null if not found
     */
    public V get(K key) {
        int index = hash(key);
        for (Node<K, V> node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.val;
            }
        }
        return null; // Key not found
    }

    /**
     * Removes the key-value pair for the specified key from the hash table.
     * @param key the key to remove
     * @return the removed value, or null if the key was not present
     */
    public V remove(K key) {
        int index = hash(key);
        List<Node<K, V>> bucket = buckets[index];
        for (Node<K, V> node : bucket) {
            if (node.key.equals(key)) {
                V removedValue = node.val;
                bucket.remove(node);
                size--;
                return removedValue;
            }
        }
        return null; // Key not found
    }

    /**
     * Returns a list of all values currently stored in the hash table.
     * Useful for iterating, e.g., to display all stored products.
     * @return a List containing every value in the hash table
     */
    public List<V> values() {
        List<V> allValues = new LinkedList<>();
        for (List<Node<K, V>> bucket : buckets) {
            for (Node<K, V> node : bucket) {
                allValues.add(node.val);
            }
        }
        return allValues;
    }

    /**
     * Returns the number of key-value pairs currently in the hash table.
     * @return size of the hash table
     */
    public int size() {
        return size;
    }
}
