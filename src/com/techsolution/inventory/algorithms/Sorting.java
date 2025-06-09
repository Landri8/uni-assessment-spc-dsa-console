package com.techsolution.inventory.algorithms;

import java.util.Arrays;

/**
 * Provides a merge sort implementation for integer arrays.
 * Merge sort is a divide-and-conquer algorithm with O(n log n) time complexity.
 */
public class Sorting {
    /**
     * Recursively sorts an integer array using merge sort.
     * @param arr the input array to sort
     * @return a new sorted array containing the same elements as arr
     */
    public static int[] mergeSort(int[] arr) {
        int n = arr.length;
        // Base case: arrays of length 0 or 1 are already sorted
        if (n <= 1) return arr;
        
        // Divide the array into two halves
        int mid = n / 2;
        // Recursively sort the left half
        int[] left = mergeSort(Arrays.copyOfRange(arr, 0, mid));
        // Recursively sort the right half
        int[] right = mergeSort(Arrays.copyOfRange(arr, mid, n));
        
        // Merge the two sorted halves and return the result
        return merge(left, right);
    }

    /**
     * Merges two sorted arrays into a single sorted array.
     * @param left  a sorted array
     * @param right a sorted array
     * @return a new array containing all elements from left and right, in sorted order
     */
    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int i = 0, j = 0, k = 0;
        
        // Compare elements from left and right, copying the smaller into result
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }
        
        // Copy any remaining elements from left (if right is exhausted)
        while (i < left.length) {
            result[k++] = left[i++];
        }
        
        // Copy any remaining elements from right (if left is exhausted)
        while (j < right.length) {
            result[k++] = right[j++];
        }
        
        return result; // Return the merged, sorted array
    }
}
