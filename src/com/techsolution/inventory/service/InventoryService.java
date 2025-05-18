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
 * Inventory and sales logic with back-order queue.
 */
public class InventoryService {
    private final HashTable<String,Product> products = new HashTable<>(16);
    private final java.util.List<SaleRecord> sales = new java.util.ArrayList<>();
    private final Queue<BackOrder> backOrders = new Queue<>();

    public void addProduct(Product p){products.put(p.getId(),p);}
    public boolean updateStock(String id,int q){var p=products.get(id); if(p==null) return false; p.setQuantity(q); return true;}
    public boolean removeProduct(String id){return products.remove(id)!=null;}
    public List<Product> getAllProducts(){return products.values();}

    /**
     * Attempts to record a sale. If stock insufficient, enqueues back-order.
     * Returns false only if product ID invalid.
     */
    public boolean recordSale(String id,int qty,double discount){
        var p=products.get(id);
        if(p==null) return false;
        if(p.getQuantity()<qty){
            backOrders.enqueue(new BackOrder(id,qty,discount));
            System.out.println("Sale queued as back-order for " + id);
            return true;
        }
        p.setQuantity(p.getQuantity()-qty);
        double amt = qty*p.getPrice()*(1-discount/100);
        sales.add(new SaleRecord(id,qty,amt));
        if(p.getQuantity()<p.getReorderLevel())
            System.out.println("** ALERT: " + id + " below reorder level! **");
        System.out.println("Sale recorded for " + id);
        return true;
    }

    /**
     * Processes pending back-orders once each, re-queuing those still unfulfillable.
     */
    public void processBackOrders(){
        if(backOrders.isEmpty()){System.out.println("No back-orders.");return;}
        int n = backOrders.size();
        for(int i=0;i<n;i++){
            var bo = backOrders.dequeue();
            var p=products.get(bo.productId);
            if(p!=null && p.getQuantity()>=bo.qty){
                p.setQuantity(p.getQuantity()-bo.qty);
                double amt = bo.qty*p.getPrice()*(1-bo.discount/100);
                sales.add(new SaleRecord(bo.productId,bo.qty,amt));
                System.out.println("Processed back-order: " + bo.productId);
            } else {
                backOrders.enqueue(bo);
                System.out.println("Still pending: " + bo.productId);
            }
        }
    }

    public void generateEndOfDayReport(){
        ReportUtil.printSeparator();
        double total = sales.stream().mapToDouble(sr->sr.amount).sum();
        Map<String,Integer> byCat = sales.stream()
            .collect(Collectors.groupingBy(
                sr->products.get(sr.productId).getCategory(),
                Collectors.summingInt(sr->sr.quantity)));
        int[] qtys = sales.stream().mapToInt(sr->sr.quantity).toArray();
        if(qtys.length>0){
            int[] sorted = Sorting.mergeSort(qtys);
            int max=sorted[sorted.length-1], min=sorted[0];
            var top = sales.stream().filter(sr->sr.quantity==max)
                           .map(sr->sr.productId).distinct().collect(Collectors.toList());
            var bot = sales.stream().filter(sr->sr.quantity==min)
                           .map(sr->sr.productId).distinct().collect(Collectors.toList());
            ReportUtil.printReport(total,byCat,top,bot);
        } else {
            System.out.println("No sales recorded today.");
        }
        ReportUtil.printSeparator();
        sales.clear();
    }

    private record BackOrder(String productId,int qty,double discount){}
    private record SaleRecord(String productId,int quantity,double amount){}
}