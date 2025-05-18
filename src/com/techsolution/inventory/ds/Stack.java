package com.techsolution.inventory.ds;

/**
 * A simple generic stack implemented via a singly linked list.
 */
public class Stack<T> {
    private static class Node<T> { T data; Node<T> next; Node(T data) { this.data = data; }}
    private Node<T> top; private int size;

    public Stack() { top = null; size = 0; }
    public void push(T item) { Node<T> node = new Node<>(item); node.next = top; top = node; size++; }
    public T pop() { if (isEmpty()) return null; T item = top.data; top = top.next; size--; return item; }
    public T peek() { return isEmpty() ? null : top.data; }
    public boolean isEmpty() { return top == null; }
    public int size() { return size; }
}
