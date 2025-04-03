package dto;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    @NotNull(message = "Buyer ID must not be null")
    private int oauthId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<@Valid OrderItemRequest> items = new ArrayList<>();

    public OrderRequest() {
    }

    @JsonbCreator
    public OrderRequest(@JsonbProperty("oauthId") int oauthId,
                        @JsonbProperty("items") List<OrderItemRequest> items) {
        this.oauthId = oauthId;
        this.items = items;
    }


    public int getOauthId() {
        return oauthId;
    }

    public void setOauthId(int oauthId) {
        this.oauthId = oauthId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}