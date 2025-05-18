package com.techsolution.inventory.model;

/**
 * Product with ID, name, category, price, quantity and reorder level.
 */
public class Product {
    private final String id;
    private final String name;
    private final String category;
    private final double price;
    private int quantity;
    private final int reorderLevel;

    public Product(String id, String name, String category,
                   double price, int quantity, int reorderLevel) {
        this.id = id; this.name = name; this.category = category;
        this.price = price; this.quantity = quantity; this.reorderLevel = reorderLevel;
    }
    public String getId(){return id;} 
    public String getName(){return name;}
    public String getCategory(){return category;} 
    public double getPrice(){return price;}
    public int getQuantity(){return quantity;} 
    public int getReorderLevel(){return reorderLevel;}
    public void setQuantity(int q){this.quantity=q;}
    
    @Override public String toString(){
        return String.format("%s [%s] - %s: %d @%.2f",
            name,id,category,quantity,price);
    }
}