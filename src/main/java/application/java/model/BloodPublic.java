package application.java.model;

import com.google.gson.annotations.SerializedName;

public class BloodPublic {
    @SerializedName(value = "asset_id")
    private String id;
    private String ownerOrg;
    @SerializedName(value = "trade_id")
    private String tradeId;
    public String publicDescription;
    public int price;

    public BloodPublic(String id, String tradeId) {
        this.id = id;
        this.tradeId = tradeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOwnerOrg() {
        return ownerOrg;
    }

    public void setOwnerOrg(String ownerOrg) {
        this.ownerOrg = ownerOrg;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }
}
