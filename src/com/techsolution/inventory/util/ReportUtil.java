package com.techsolution.inventory.util;

import java.util.List;
import java.util.Map;

/**
 * Utility class for formatting and printing the end-of-day sales report to the console.
 * Provides methods to print separators and a detailed report including total revenue,
 * sales breakdown by category, and top/bottom selling products.
 */
public class ReportUtil {
    /**
     * Prints a visual separator line to the console, used to delineate report sections.
     */
    public static void printSeparator() {
        System.out.println("================================");
    }

    /**
     * Prints the end-of-day sales report with the following details:
     *  - Total revenue (formatted to two decimal places)
     *  - Sales by category breakdown (category name and total units sold)
     *  - Top-selling product IDs
     *  - Bottom-selling product IDs
     * @param total   the total revenue generated from all sales
     * @param byCat   a map where each key is a product category and the value is total units sold in that category
     * @param top     a list of product IDs that sold the most units (could be multiple if there's a tie)
     * @param bottom  a list of product IDs that sold the fewest units (could be multiple if there's a tie)
     */
    public static void printReport(double total,
                                   Map<String, Integer> byCat,
                                   List<String> top,
                                   List<String> bottom) {
        System.out.println();
        // Print total revenue with two decimal places
        System.out.printf("Total Revenue: %.2f%n", total);
        
        // Print sales breakdown by category
        System.out.println("Sales by Category:");
        byCat.forEach((category, quantity) ->
            System.out.printf("  %s: %d%n", category, quantity)
        );
        
        // Print top-selling product IDs
        System.out.println("Top Seller(s): " + String.join(", ", top));
        
        // Print bottom-selling product IDs
        System.out.println("Bottom Seller(s): " + String.join(", ", bottom));
    }
}
