package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VendorDTO {
    @JsonProperty("oauth_id")
    private int oauthId;

    @JsonProperty("store_name")
    private String storeName;

    public VendorDTO() {}

    public VendorDTO(int oauthId, String storeName) {
        this.oauthId = oauthId;
        this.storeName = storeName;
    }

    public int getOauthId() {
        return oauthId;
    }

    public void setOauthId(int oauthId) {
        this.oauthId = oauthId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}