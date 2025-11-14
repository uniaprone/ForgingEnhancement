package org.zzq.forgingEnhancement.services;

import com.google.gson.Gson;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.managers.KeyManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;
import org.zzq.forgingEnhancement.models.ForgingAttribute;

public class NBTService {

    private final Gson gson = new Gson();
    private final KeyManager keyManager;
    public NBTService(KeyManager keyManager){
        this.keyManager = keyManager;
    }
    public void storeForgingNBT(ItemMeta itemMeta, EnhancementResult enhancementResult) {
        String AttributesString = gson.toJson(enhancementResult);
        itemMeta.getPersistentDataContainer().set(keyManager.getEnhancementKey(),  PersistentDataType.STRING, AttributesString);
    }

    public void storeForgingAttributeGUINBT(ItemMeta itemMeta, ForgingAttribute forgingAttribute) {
        String AttributeString = gson.toJson(forgingAttribute);
        itemMeta.getPersistentDataContainer().set(keyManager.getForgingAttributeGUIKey(),  PersistentDataType.STRING, AttributeString);
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
    public String getForgingStringNBT(ItemMeta meta){
        return meta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING);
    }

    public boolean hasForgingAttributeGUINBT(ItemMeta meta){
        return meta.getPersistentDataContainer().get(keyManager.getForgingAttributeGUIKey(), PersistentDataType.STRING) != null;
    }

    public ForgingAttribute getForgingAttributeGUINBT(ItemMeta meta){
        String attributeString =  meta.getPersistentDataContainer().get(keyManager.getForgingAttributeGUIKey(), PersistentDataType.STRING);
        return gson.fromJson(attributeString, ForgingAttribute.class);
    }

    public EnhancementResult getForgingNBT(ItemMeta meta){
        String AttributesString = meta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING);
        return gson.fromJson(AttributesString, EnhancementResult.class);
    }
    public void removeForgingNBT(ItemMeta meta){
        meta.getPersistentDataContainer().remove(keyManager.getEnhancementKey());
    }
}
