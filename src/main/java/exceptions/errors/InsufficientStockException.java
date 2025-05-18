package exceptions.errors;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(int productId, int requestedQuantity, int availableStock) {
        super("Insufficient stock for product with id " + productId + ": requested " + requestedQuantity + ", available " + availableStock);
    }
}
