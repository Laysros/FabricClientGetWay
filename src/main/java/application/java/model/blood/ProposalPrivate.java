package application.java.model.blood;

import com.google.gson.annotations.SerializedName;

public class ProposalPrivate {
    @SerializedName(value = "proposal_id")
    private String id;
    private String bloodType;
    private String signature;

    public ProposalPrivate(String id, String bloodType, String signature) {
        this.id = id;
        this.bloodType = bloodType;
        this.signature = signature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
