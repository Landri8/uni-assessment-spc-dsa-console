package com.techsolution.inventory.util;

import java.util.List;
import java.util.Map;

/**
 * Utility for printing end-of-day reports in a formatted way.
 */
public class ReportUtil {
    public static void printSeparator(){ System.out.println("================================"); }
    public static void printReport(double total,Map<String,Integer> byCat,
                                   List<String> top,List<String> bottom){
    	System.out.println();
        System.out.printf("Total Revenue: %.2f%n", total);
        System.out.println("Sales by Category:");
        byCat.forEach((c,q)-> System.out.printf("  %s: %d%n",c,q));
        System.out.println("Top Seller(s): " + String.join(", ", top));
        System.out.println("Bottom Seller(s): " + String.join(", ", bottom));
    }
}