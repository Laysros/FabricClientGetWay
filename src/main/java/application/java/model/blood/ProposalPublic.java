package application.java.model.blood;

import com.google.gson.annotations.SerializedName;

public class ProposalPublic {
    @SerializedName(value = "proposal_id")
    private String id;
    private String fromOrg;
    private String toOrg;
    private int amount;
    @SerializedName(value = "trade_id")
    private String tradeId;
    public String description;

    public ProposalPublic(String id, String fromOrg, String toOrg, int amount, String tradeId, String description) {
        this.id = id;
        this.fromOrg = fromOrg;
        this.toOrg = toOrg;
        this.amount = amount;
        this.tradeId = tradeId;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromOrg() {
        return fromOrg;
    }

    public void setFromOrg(String fromOrg) {
        this.fromOrg = fromOrg;
    }

    public String getToOrg() {
        return toOrg;
    }

    public void setToOrg(String toOrg) {
        this.toOrg = toOrg;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
