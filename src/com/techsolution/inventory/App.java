package com.techsolution.inventory;

import com.techsolution.inventory.model.Product;
import com.techsolution.inventory.service.InventoryService;

import java.util.List;
import java.util.Scanner;

/**
 * Console application for Inventory Management, featuring:
 *  - Add/update/remove products
 *  - Record sales and enqueue back-orders
 *  - Process pending back-orders (FIFO queue)
 *  - View products with sorting and search filters
 *  - Generate end-of-day reports using merge sort
 */
public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InventoryService service = new InventoryService();

    public static void main(String[] args) {
        System.out.println("=== Inventory Management System ===");
        boolean exit = false;
        while (!exit) {
            printMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> addProduct();
                case "2" -> updateProduct();
                case "3" -> removeProduct();
                case "4" -> recordSale();
                case "5" -> processBackOrders();
                case "6" -> viewProducts();
                case "7" -> service.generateEndOfDayReport();
                case "8" -> exit = true;
                default -> System.out.println("Invalid option. Please choose 1-8.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
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

    private static void addProduct() {
        System.out.println("-- Add New Product (type 'exit' to cancel) --");
        String productId;

        while (true) {
            System.out.print("Product ID: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) return;

            final String idToCheck = input;
            boolean exists = service.getAllProducts().stream()
                                    .anyMatch(p -> p.getId().equals(idToCheck));

            if (exists) {
                System.out.println("ID exists. Enter unique ID or 'exit'.");
            } else {
                productId = idToCheck;
                break;
            }
        }

        System.out.print("Name: ");
        String name = scanner.nextLine().trim(); if (name.equalsIgnoreCase("exit")) return;

        System.out.print("Category: ");
        String category = scanner.nextLine().trim(); if (category.equalsIgnoreCase("exit")) return;

        System.out.print("Price: ");
        double price = parseDouble(true); if (price < 0) return;

        System.out.print("Quantity: ");
        int qty = parseInt(true); if (qty < 0) return;

        System.out.print("Reorder Level: ");
        int reorder = parseInt(true); if (reorder < 0) return;

        service.addProduct(new Product(productId, name, category, price, qty, reorder));
        System.out.println("Product added.");
    }


    private static void updateProduct() {
        System.out.println("-- Update Stock (type 'exit' to cancel) --");
        List<Product> list = service.getAllProducts();
        if (list.isEmpty()) { System.out.println("No products."); return; }
        System.out.println("Available Products:");
        list.forEach(p -> System.out.println("  " + p));
        System.out.print("ID: ");
        String id = scanner.nextLine().trim(); if (id.equalsIgnoreCase("exit")) return;
        if (list.stream().noneMatch(p->p.getId().equals(id))) { System.out.println("Invalid ID."); return; }
        System.out.print("New Quantity: ");
        int q = parseInt(true); if (q<0) return;
        service.updateStock(id,q);
        System.out.println("Stock updated for product " + id + ".");
    }

    private static void removeProduct() {
        System.out.println("-- Remove Product (type 'exit') --");
        List<Product> list = service.getAllProducts();
        if (list.isEmpty()) { System.out.println("No products."); return; }
        System.out.println("Available Products:");
        list.forEach(p -> System.out.println("  " + p));
        System.out.print("ID: ");
        String id = scanner.nextLine().trim(); if (id.equalsIgnoreCase("exit")) return;
        if (service.removeProduct(id)) System.out.println("Removed product " + id + ".");
        else System.out.println("Not found: " + id + ".");
    }

    private static void recordSale() {
        System.out.println("-- Record Sale (type 'exit') --");
        List<Product> list = service.getAllProducts();
        if (list.isEmpty()) { System.out.println("No products to sell."); return; }
        System.out.println("Available Products:");
        list.forEach(p -> System.out.println("  " + p));
        System.out.print("ID: ");
        String id = scanner.nextLine().trim(); if (id.equalsIgnoreCase("exit")) return;
        System.out.print("Qty: ");
        int q = parseInt(true); if (q<0) return;
        System.out.print("Discount%: ");
        double d = parseDouble(true); if (d<0) return;
        if (!service.recordSale(id,q,d)) {
            System.out.println("Sale failed for " + id + ". Check stock or ID.");
        }
    }

    private static void processBackOrders() {
        System.out.println("-- Process Back-Orders --");
        service.processBackOrders();
    }

    private static void viewProducts() {
        while (true) {
        	System.out.println();
            System.out.println("-- View Products --");
            System.out.println("1.Price ↑  2.Price ↓  3.Name ↑  4.Name ↓  5.Search Name  6.Search Category  7.Back");
            System.out.print("Select: ");
            String c = scanner.nextLine().trim();
            if (c.equals("7")) return;
            List<Product> list = service.getAllProducts();
            switch (c) {
                case "1" -> list.stream()
                                 .sorted((a,b)-> Double.compare(a.getPrice(),b.getPrice()))
                                 .forEach(System.out::println);
                case "2" -> list.stream()
                                 .sorted((a,b)-> Double.compare(b.getPrice(),a.getPrice()))
                                 .forEach(System.out::println);
                case "3" -> list.stream()
                                 .sorted((a,b)-> a.getName().compareToIgnoreCase(b.getName()))
                                 .forEach(System.out::println);
                case "4" -> list.stream()
                                 .sorted((a,b)-> b.getName().compareToIgnoreCase(a.getName()))
                                 .forEach(System.out::println);
                case "5" -> {
                    System.out.print("Keyword: ");
                    String kw = scanner.nextLine().trim().toLowerCase();
                    list.stream()
                        .filter(p->p.getName().toLowerCase().contains(kw))
                        .forEach(System.out::println);
                }
                case "6" -> {
                    System.out.print("Category: ");
                    String cat = scanner.nextLine().trim().toLowerCase();
                    list.stream()
                        .filter(p->p.getCategory().toLowerCase().equals(cat))
                        .forEach(System.out::println);
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static int parseInt(boolean allowExit) {
        while (true) {
            String l = scanner.nextLine().trim();
            if (allowExit && l.equalsIgnoreCase("exit")) return -1;
            try { return Integer.parseInt(l); }
            catch (Exception e) { System.out.print("Enter valid integer: "); }
        }
    }

    private static double parseDouble(boolean allowExit) {
        while (true) {
            String l = scanner.nextLine().trim();
            if (allowExit && l.equalsIgnoreCase("exit")) return -1;
            try { return Double.parseDouble(l); }
            catch (Exception e) { System.out.print("Enter valid number: "); }
        }
    }
}

