package org.zzq.forgingEnhancement.domain.entity;

public class PlayerSetting {
    private String UUID;
    private String name;
    private boolean isEnable;

    public PlayerSetting(String UUID, String name, boolean isEnable) {
        this.UUID = UUID;
        this.name = name;
        this.isEnable = isEnable;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean toggleEnable(){
        isEnable = !isEnable;
        return isEnable;
    }
}
