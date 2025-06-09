package com.techsolution.inventory;

import com.techsolution.inventory.model.Product;
import com.techsolution.inventory.service.InventoryService;

import java.util.List;
import java.util.Scanner;

/**
 * Main console application for Inventory Management.
 * Supports adding, updating, removing products;
 * recording sales with back-order queuing;
 * processing pending back-orders;
 * viewing products with sorting/search filters;
 * and generating end-of-day reports.
 */
public class App {
    // Scanner for reading user input from console
    private static final Scanner scanner = new Scanner(System.in);
    // Service layer instance that uses custom data structures and algorithms
    private static final InventoryService service = new InventoryService();

    public static void main(String[] args) {
        System.out.println("=== Inventory Management System ===");
        boolean exit = false;
        // Main application loop: show menu until user chooses to exit
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addProduct();            // Add a new product
                case "2" -> updateProduct();       // Update stock for an existing product
                case "3" -> removeProduct();       // Remove a product by ID
                case "4" -> recordSale();          // Record a sale or enqueue back-order
                case "5" -> processBackOrders();   // Process all pending back-orders
                case "6" -> viewProducts();        // View or search products
                case "7" -> service.generateEndOfDayReport(); // Show end-of-day financial report
                case "8" -> exit = true;           // Exit the application
                default -> System.out.println("Invalid option. Please choose 1-8.");
            }
        }
        System.out.println("Goodbye!");
    }

    /**
     * Displays the main menu options to the console.
     */
    private static void printMenu() {
        System.out.println();
        System.out.println("1. Add new product");
        System.out.println("2. Update product stock");
        System.out.println("3. Remove product");
        System.out.println("4. Record sale");
        System.out.println("5. Process back-orders");
        System.out.println("6. View products");
        System.out.println("7. End-of-day report");
        System.out.println("8. Exit");
        System.out.println();
        System.out.print("Select (1-8): ");
    }

    /**
     * Handles adding a new product.
     * Prompts for ID, ensures uniqueness; then prompts for name, category, price, quantity, reorder level.
     * User may type 'exit' at any prompt to cancel and return to the main menu.
     */
    private static void addProduct() {
        System.out.println("-- Add New Product (type 'exit' to cancel) --");
        String productId;

        // Loop until a unique product ID is entered or user cancels
        while (true) {
            System.out.print("Product ID: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                return; // Cancel operation
            }
            final String idToCheck = input;
            // Check if the entered ID already exists in the inventory
            boolean exists = service.getAllProducts().stream()
                                    .anyMatch(p -> p.getId().equals(idToCheck));

            if (exists) {
                System.out.println("ID exists. Enter unique ID or 'exit'.");
            } else {
                productId = idToCheck;
                break;
            }
        }

        // Prompt for product name
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (name.equalsIgnoreCase("exit")) return;

        // Prompt for product category
        System.out.print("Category: ");
        String category = scanner.nextLine().trim();
        if (category.equalsIgnoreCase("exit")) return;

        // Prompt for product price and validate numeric input
        System.out.print("Price: ");
        double price = parseDouble(true);
        if (price < 0) return; // User typed 'exit'

        // Prompt for initial stock quantity
        System.out.print("Quantity: ");
        int qty = parseInt(true);
        if (qty < 0) return; // User typed 'exit'

        // Prompt for reorder level threshold
        System.out.print("Reorder Level: ");
        int reorder = parseInt(true);
        if (reorder < 0) return; // User typed 'exit'

        // Create and add the new product to the service (HashTable storage)
        service.addProduct(new Product(productId, name, category, price, qty, reorder));
        System.out.println("Product added.");
    }

    /**
     * Handles updating the stock quantity of an existing product.
     * Displays all products and their details, prompts for ID and new quantity.
     * User may type 'exit' to cancel at any time.
     */
    private static void updateProduct() {
        System.out.println("-- Update Stock (type 'exit' to cancel) --");
        List<Product> list = service.getAllProducts();
        if (list.isEmpty()) {
            System.out.println("No products.");
            return;
        }
        // Show current products with full details
        System.out.println("Available Products:");
        list.forEach(p -> System.out.println("  " + p));

        // Prompt for product ID to update
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        if (id.equalsIgnoreCase("exit")) return;
        // Validate that the ID exists
        if (list.stream().noneMatch(p -> p.getId().equals(id))) {
            System.out.println("Invalid ID.");
            return;
        }

        // Prompt for new stock quantity
        System.out.print("New Quantity: ");
        int newQty = parseInt(true);
        if (newQty < 0) return; // User typed 'exit'

        // Update stock in the InventoryService (HashTable lookup)
        service.updateStock(id, newQty);
        System.out.println("Stock updated for product " + id + ".");
    }

    /**
     * Handles removing a product from inventory.
     * Displays all products, prompts for ID, and removes if found.
     * User may type 'exit' to cancel.
     */
    private static void removeProduct() {
        System.out.println("-- Remove Product (type 'exit') --");
        List<Product> list = service.getAllProducts();
        if (list.isEmpty()) {
            System.out.println("No products.");
            return;
        }
        // Show current products with full details
        System.out.println("Available Products:");
        list.forEach(p -> System.out.println("  " + p));

        // Prompt for product ID to remove
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        if (id.equalsIgnoreCase("exit")) return;

        // Attempt removal; notify user of success or failure
        if (service.removeProduct(id)) {
            System.out.println("Removed product " + id + ".");
        } else {
            System.out.println("Not found: " + id + ".");
        }
    }

    /**
     * Handles recording a sale.
     * Shows current products, prompts for ID, quantity, and discount.
     * If stock is sufficient, reduces stock and records revenue.
     * If stock is insufficient, enqueues a back-order.
     * User may type 'exit' to cancel.
     */
    private static void recordSale() {
        System.out.println("-- Record Sale (type 'exit') --");
        List<Product> list = service.getAllProducts();
        if (list.isEmpty()) {
            System.out.println("No products to sell.");
            return;
        }
        // Show current products with full details
        System.out.println("Available Products:");
        list.forEach(p -> System.out.println("  " + p));

        // Prompt for product ID to sell
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        if (id.equalsIgnoreCase("exit")) return;

        // Prompt for quantity to sell
        System.out.print("Qty: ");
        int qty = parseInt(true);
        if (qty < 0) return; // User typed 'exit'

        // Prompt for discount percentage
        System.out.print("Discount%: ");
        double discount = parseDouble(true);
        if (discount < 0) return; // User typed 'exit'

        // Attempt to record the sale via InventoryService
        if (!service.recordSale(id, qty, discount)) {
            System.out.println("Sale failed for " + id + ". Check stock or ID.");
        }
    }

    /**
     * Processes all pending back-orders in FIFO order.
     * Calls the service layer to attempt fulfillment of each queued sale.
     */
    private static void processBackOrders() {
        System.out.println("-- Process Back-Orders --");
        service.processBackOrders();
    }

    /**
     * Displays a menu for viewing products with various filters:
     * - Sort by price ascending/descending
     * - Sort by name ascending/descending
     * - Search by name keyword
     * - Search by category
     * Repeats until user chooses to go back.
     */
    private static void viewProducts() {
        while (true) {
            System.out.println();
            System.out.println("-- View Products --");
            System.out.println("1.Price ↑  2.Price ↓  3.Name ↑  4.Name ↓  5.Search Name  6.Search Category  7.Back");
            System.out.print("Select: ");
            String choice = scanner.nextLine().trim();

            // Exit this view menu if user selects "7"
            if (choice.equals("7")) return;

            // Fetch the list of all products for filtering/sorting
            List<Product> list = service.getAllProducts();

            switch (choice) {
                case "1" -> // Sort by price ascending
                    list.stream()
                        .sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
                        .forEach(System.out::println);
                case "2" -> // Sort by price descending
                    list.stream()
                        .sorted((a, b) -> Double.compare(b.getPrice(), a.getPrice()))
                        .forEach(System.out::println);
                case "3" -> // Sort by name ascending (A–Z)
                    list.stream()
                        .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                        .forEach(System.out::println);
                case "4" -> // Sort by name descending (Z–A)
                    list.stream()
                        .sorted((a, b) -> b.getName().compareToIgnoreCase(a.getName()))
                        .forEach(System.out::println);
                case "5" -> { // Search products by name keyword
                    System.out.print("Keyword: ");
                    String kw = scanner.nextLine().trim().toLowerCase();
                    list.stream()
                        .filter(p -> p.getName().toLowerCase().contains(kw))
                        .forEach(System.out::println);
                }
                case "6" -> { // Search products by exact category match
                    System.out.print("Category: ");
                    String cat = scanner.nextLine().trim().toLowerCase();
                    list.stream()
                        .filter(p -> p.getCategory().toLowerCase().equals(cat))
                        .forEach(System.out::println);
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Parses an integer from user input. If allowExit is true,
     * typing 'exit' returns -1. Otherwise, loops until a valid integer is entered.
     * @param allowExit whether to allow "exit" to cancel and return -1
     * @return parsed integer or -1 if user typed 'exit'
     */
    private static int parseInt(boolean allowExit) {
        while (true) {
            String line = scanner.nextLine().trim();
            if (allowExit && line.equalsIgnoreCase("exit")) {
                return -1;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Enter valid integer: ");
            }
        }
    }

    /**
     * Parses a double from user input. If allowExit is true,
     * typing 'exit' returns -1. Otherwise, loops until a valid number is entered.
     * @param allowExit whether to allow "exit" to cancel and return -1
     * @return parsed double or -1 if user typed 'exit'
     */
    private static double parseDouble(boolean allowExit) {
        while (true) {
            String line = scanner.nextLine().trim();
            if (allowExit && line.equalsIgnoreCase("exit")) {
                return -1;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Enter valid number: ");
            }
        }
    }
}
