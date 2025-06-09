package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VendorDTO {
    @JsonProperty("keycloak_id")
    private String keycloakId;

    @JsonProperty("store_name")
    private String storeName;

    public VendorDTO() {}

    public VendorDTO(String keycloakId, String storeName) {
        this.keycloakId = keycloakId;
        this.storeName = storeName;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}