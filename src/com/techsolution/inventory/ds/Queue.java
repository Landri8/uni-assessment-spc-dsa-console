package com.techsolution.inventory.ds;

/**
 * A simple generic FIFO (first-in, first-out) queue implementation using a singly linked list.
 * Enqueue and dequeue operations both run in O(1) time.
 * @param <T> the type of elements held in the queue
 */
public class Queue<T> {
    /**
     * Node in the linked list storing a single data item and a reference to the next node.
     */
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    // Reference to the front (head) of the queue
    private Node<T> head;
    // Reference to the rear (tail) of the queue
    private Node<T> tail;
    // Number of elements currently in the queue
    private int size;

    /**
     * Adds an item to the rear of the queue.
     * @param item the element to enqueue
     */
    public void enqueue(T item) {
        Node<T> node = new Node<>(item);
        if (tail != null) {
            tail.next = node; // Link the old tail to the new node
        }
        tail = node; // Update tail to the new node
        if (head == null) {
            // If queue was empty, head should also point to the new node
            head = node;
        }
        size++;
    }

    /**
     * Removes and returns the item at the front of the queue.
     * @return the dequeued element, or null if the queue is empty
     */
    public T dequeue() {
        if (head == null) {
            return null; // Queue is empty
        }
        T data = head.data;
        head = head.next; // Move head to the next node
        if (head == null) {
            // If the queue becomes empty after removal, reset tail to null
            tail = null;
        }
        size--;
        return data;
    }

    /**
     * Returns true if the queue has no elements.
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Returns the number of items currently in the queue.
     * @return the size of the queue
     */
    public int size() {
        return size;
    }
}
