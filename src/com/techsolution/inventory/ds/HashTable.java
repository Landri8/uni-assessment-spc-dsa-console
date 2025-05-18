package com.techsolution.inventory.ds;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple generic hash table with separate chaining for collision handling.
 */
public class HashTable<K,V> {
    private static class Node<K,V>{K key;V val;Node(K k,V v){key=k;val=v;}}
    private final List<Node<K,V>>[] buckets; private int size;
    @SuppressWarnings("unchecked") public HashTable(int cap){
        buckets=new LinkedList[cap]; for(int i=0;i<cap;i++) buckets[i]=new LinkedList<>();
    }
    private int hash(K k){return Math.abs(k.hashCode())%buckets.length;}
    public void put(K k,V v){var b=buckets[hash(k)];
        for(var n:b) if(n.key.equals(k)){n.val=v;return;} b.add(new Node<>(k,v)); size++; }
    public V get(K k){for(var n:buckets[hash(k)]) if(n.key.equals(k)) return n.val; return null;}
    public V remove(K k){var b=buckets[hash(k)]; for(var n:b) if(n.key.equals(k)){V v=n.val; b.remove(n); size--; return v;} return null;}
    public List<V> values(){
        var list=new LinkedList<V>();
        for(var b:buckets) for(var n:b) list.add(n.val);
        return list;
    }
    public int size(){return size;}
}