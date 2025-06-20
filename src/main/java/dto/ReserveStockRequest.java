package dto;

import java.util.List;

public class ReserveStockRequest {
    public List<ReserveStockItem> items;

    public ReserveStockRequest() {}

    public ReserveStockRequest(List<ReserveStockItem> items) {
        this.items = items;
    }

    public List<ReserveStockItem> getItems() {
        return items;
    }

    public void setItems(List<ReserveStockItem> items) {
        this.items = items;
    }
}