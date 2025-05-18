package com.techsolution.inventory.algorithms;


/**
 * Provides bubble sort and merge sort implementations for integer arrays.
 */
public class Sorting {
    public static int[] mergeSort(int[] arr) {
        int n = arr.length;
        if (n <= 1) return arr;
        int mid = n/2;
        int[] L = mergeSort(java.util.Arrays.copyOfRange(arr,0,mid));
        int[] R = mergeSort(java.util.Arrays.copyOfRange(arr,mid,n));
        int[] res = new int[n]; int i=0,j=0,k=0;
        while(i<L.length && j<R.length) res[k++] = (L[i]<=R[j]?L[i++]:R[j++]);
        while(i<L.length) res[k++]=L[i++];
        while(j<R.length) res[k++]=R[j++];
        return res;
    }
}