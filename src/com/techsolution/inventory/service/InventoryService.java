package com.techsolution.inventory.service;

import com.techsolution.inventory.ds.HashTable;
import com.techsolution.inventory.ds.Queue;
import com.techsolution.inventory.model.Product;
import com.techsolution.inventory.util.ReportUtil;
import com.techsolution.inventory.algorithms.Sorting;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for inventory and sales management, including back-order support.
 * Uses a custom HashTable to store products and a custom Queue for pending back-orders.
 */
public class InventoryService {
    // Hash table mapping product ID to Product object for O(1) average lookup
    private final HashTable<String, Product> products = new HashTable<>(16);
    // List to record successful sales (productId, quantity, amount)
    private final java.util.List<SaleRecord> sales = new java.util.ArrayList<>();
    // FIFO queue to hold back-order requests when stock is insufficient
    private final Queue<BackOrder> backOrders = new Queue<>();

    /**
     * Adds a new product to the inventory.
     * @param p Product to add
     */
    public void addProduct(Product p) {
        products.put(p.getId(), p);
    }

    /**
     * Updates the stock level for an existing product.
     * @param id product ID
     * @param quantity new stock quantity
     * @return true if product exists and was updated, false if ID not found
     */
    public boolean updateStock(String id, int quantity) {
        Product p = products.get(id);      // O(1) average lookup in hash table
        if (p == null) {
            return false;                  // Product ID invalid
        }
        p.setQuantity(quantity);
        return true;
    }

    /**
     * Removes a product from the inventory by its ID.
     * @param id product ID
     * @return true if a product was removed, false if ID not found
     */
    public boolean removeProduct(String id) {
        return products.remove(id) != null; // returns null if no such key
    }

    /**
     * Returns a list of all stored Product objects.
     * Used when displaying or iterating over inventory.
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        return products.values(); // collects values from all hash table buckets
    }

    /**
     * Attempts to record a sale. If the requested quantity exceeds available stock,
     * creates a BackOrder and enqueues it instead. If the product ID is invalid,
     * returns false.
     * @param id product ID
     * @param qty quantity requested to sell
     * @param discount discount percentage to apply (0-100)
     * @return true if sale was recorded or back-ordered, false if ID invalid
     */
    public boolean recordSale(String id, int qty, double discount) {
        Product p = products.get(id); // O(1) lookup
        if (p == null) {
            return false;              // Invalid product ID
        }
        // If there is not enough stock, enqueue a back-order and notify user
        if (p.getQuantity() < qty) {
            backOrders.enqueue(new BackOrder(id, qty, discount)); // O(1)
            System.out.println("Sale queued as back-order for " + id);
            return true;
        }
        // Sufficient stock: deduct quantity and record sale
        p.setQuantity(p.getQuantity() - qty);
        double amount = qty * p.getPrice() * (1 - discount / 100);
        sales.add(new SaleRecord(id, qty, amount));
        // If stock falls below reorder level after sale, show an alert
        if (p.getQuantity() < p.getReorderLevel()) {
            System.out.println("** ALERT: " + id + " below reorder level! **");
        }
        System.out.println("Sale recorded for " + id);
        return true;
    }

    /**
     * Processes each pending back-order exactly once in FIFO order.
     * For each back-order, attempts to fulfill the sale if stock is now sufficient;
     * otherwise, re-enqueues the back-order to remain pending.
     */
    public void processBackOrders() {
        if (backOrders.isEmpty()) {
            System.out.println("No back-orders.");
            return;
        }
        // Only process as many orders as currently queued (size may change in loop)
        int count = backOrders.size();
        for (int i = 0; i < count; i++) {
            BackOrder bo = backOrders.dequeue(); // O(1)
            Product p = products.get(bo.productId);
            if (p != null && p.getQuantity() >= bo.qty) {
                // Fulfill the back-order as a normal sale
                p.setQuantity(p.getQuantity() - bo.qty);
                double amount = bo.qty * p.getPrice() * (1 - bo.discount / 100);
                sales.add(new SaleRecord(bo.productId, bo.qty, amount));
                System.out.println("Processed back-order: " + bo.productId);
            } else {
                // Stock still insufficient or invalid ID: re-enqueue
                backOrders.enqueue(bo);
                System.out.println("Still pending: " + bo.productId);
            }
        }
    }

    /**
     * Generates and prints the end-of-day report. Report includes:
     * - Total revenue for all recorded sales
     * - Sales breakdown by product category
     * - Top and bottom selling product IDs by units sold
     * Uses merge sort to find the extremes in O(n log n) time.
     */
    public void generateEndOfDayReport() {
        ReportUtil.printSeparator();
        // Sum all sale amounts for total revenue - O(n)
        double totalRevenue = sales.stream().mapToDouble(sr -> sr.amount).sum();
        // Group and sum quantity by category - O(n)
        Map<String, Integer> salesByCategory = sales.stream()
            .collect(Collectors.groupingBy(
                sr -> products.get(sr.productId).getCategory(),
                Collectors.summingInt(sr -> sr.quantity)
            ));
        // Extract quantities into an array for sorting
        int[] quantities = sales.stream().mapToInt(sr -> sr.quantity).toArray();

        if (quantities.length > 0) {
            // Use merge sort (O(n log n)) to sort quantities
            int[] sorted = Sorting.mergeSort(quantities);
            int min = sorted[0];
            int max = sorted[sorted.length - 1];
            
            // Find all product IDs that match the min or max sold quantities
            List<String> topSellers = sales.stream()
                .filter(sr -> sr.quantity == max)
                .map(sr -> sr.productId)
                .distinct()
                .collect(Collectors.toList());
            List<String> bottomSellers = sales.stream()
                .filter(sr -> sr.quantity == min)
                .map(sr -> sr.productId)
                .distinct()
                .collect(Collectors.toList());

            ReportUtil.printReport(totalRevenue, salesByCategory, topSellers, bottomSellers);
        } else {
            // No sales today: inform user
            System.out.println("No sales recorded today.");
        }
        ReportUtil.printSeparator();
        // Clear the sales list for the next day (reset daily totals)
        sales.clear();
    }

    /**
     * Record type holding a back-order request.
     * Stores the product ID, requested quantity, and discount.
     */
    private record BackOrder(String productId, int qty, double discount) {}

    /**
     * Record type holding a finalized sale record.
     * Stores the product ID, quantity sold, and total sale amount.
     */
    private record SaleRecord(String productId, int quantity, double amount) {}
}
