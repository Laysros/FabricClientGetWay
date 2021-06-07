package application.java.model;

import com.google.gson.annotations.SerializedName;

public class BloodPrivate {
    @SerializedName(value = "asset_id")
    private String id;
    private String group;
    private int volume;
    private String owner;
    private String salt;
    public BloodPrivate(String id, String group, int volume, String owner, String salt) {
        this.id = id;
        this.group = group;
        this.volume = volume;
        this.owner = owner;
        this.salt = salt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
