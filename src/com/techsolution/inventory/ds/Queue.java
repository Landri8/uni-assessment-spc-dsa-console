package com.techsolution.inventory.ds;

/**
 * A simple generic queue implemented via a linked list.
 */
public class Queue<T> {
    private static class Node<T>{T data;Node<T> next;Node(T d){data=d;}}
    private Node<T> head,tail;private int size;
    public void enqueue(T i){var n=new Node<>(i); if(tail!=null) tail.next=n; tail=n; if(head==null) head=n; size++;}
    public T dequeue(){if(head==null) return null;T d=head.data; head=head.next; if(head==null) tail=null; size--;return d;}
    public boolean isEmpty(){return head==null;} public int size(){return size;}
}