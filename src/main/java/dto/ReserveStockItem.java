package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReserveStockItem {
    @JsonProperty("product_id")
    public String productId;
    
    @JsonProperty("quantity")
    public int quantity;
    
    public ReserveStockItem() {}
    
    public ReserveStockItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}