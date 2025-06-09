package com.techsolution.inventory.model;

/**
 * Represents a product in the inventory with identifying details and stock information.
 * Each product has a unique ID, a name, a category, a unit price, a current quantity,
 * and a reorder level threshold.
 */
public class Product {
    // Unique identifier for this product (e.g., "P001")
    private final String id;
    // Human-readable name of the product (e.g., "Widget A")
    private final String name;
    // Category used for grouping (e.g., "Hardware", "Electronics")
    private final String category;
    // Price per unit of this product (e.g., 12.99)
    private final double price;
    // Current stock level (number of units available)
    private int quantity;
    // Quantity threshold at which a reorder alert should be triggered
    private final int reorderLevel;

    /**
     * Constructs a new Product with the given details.
     * @param id           unique product ID
     * @param name         product name
     * @param category     product category
     * @param price        price per unit
     * @param quantity     initial stock quantity
     * @param reorderLevel stock threshold for low-stock alert
     */
    public Product(String id, String name, String category,
                   double price, int quantity, int reorderLevel) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    /** @return the unique product ID */
    public String getId() { return id; }

    /** @return the product name */
    public String getName() { return name; }

    /** @return the category of this product */
    public String getCategory() { return category; }

    /** @return the price per unit */
    public double getPrice() { return price; }

    /** @return the current stock quantity */
    public int getQuantity() { return quantity; }

    /** @return the reorder level threshold */
    public int getReorderLevel() { return reorderLevel; }

    /**
     * Updates the stock quantity to a new value.
     * @param quantity the new stock quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns a formatted string describing the product, including its name,
     * ID, category, current quantity, and price. Used for console display.
     * Example: "Widget A [P001] - Hardware: 50 @12.99"
     */
    @Override
    public String toString() {
        return String.format("%s [%s] - %s: %d @%.2f",
            name, id, category, quantity, price);
    }
}
