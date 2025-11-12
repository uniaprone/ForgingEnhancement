package org.zzq.forgingEnhancement.services;

import com.google.gson.Gson;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;

public class NBTService {

    private final Gson gson = new Gson();
    private KeyManager keyManager;
    public NBTService(KeyManager keyManager){
        this.keyManager = keyManager;
    }
    public void storeForgingNBT(ItemMeta itemMeta, NamespacedKey namespacedKey, EnhancementResult enhancementResult) {
        String AttributesString = gson.toJson(enhancementResult);
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, AttributesString);
    }
    public void markBaseAttributeApplied(ItemMeta meta) {
        meta.getPersistentDataContainer().set(keyManager.getBaseAttributeKey(), PersistentDataType.BYTE, (byte) 1);
    }
    public boolean hasBaseAttributeApplied(ItemMeta meta) {
        return meta.getPersistentDataContainer().get(keyManager.getBaseAttributeKey(), PersistentDataType.BYTE) != null;
    }
    public boolean hasForgingNBT(ItemMeta meta){
        return meta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING) != null;
    }
    public EnhancementResult getForgingNBT(ItemMeta meta){
        String AttributesString = meta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING);
        return gson.fromJson(AttributesString, EnhancementResult.class);
    }
    public void removeForgingNBT(ItemMeta meta){
        meta.getPersistentDataContainer().remove(keyManager.getEnhancementKey());
    }
}
