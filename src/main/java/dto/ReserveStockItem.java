package dto;

public class ReserveStockItem {
    public String productId; // Changed from int to String
    public int quantity;

    public ReserveStockItem() {}

    public ReserveStockItem(String productId, int quantity) { // Changed parameter type
        this.productId = productId;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ReserveStockItem{productId='" + productId + "', quantity=" + quantity + "}";
    }
}