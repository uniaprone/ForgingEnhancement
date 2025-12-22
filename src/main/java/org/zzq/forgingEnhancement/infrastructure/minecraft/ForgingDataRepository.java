package org.zzq.forgingEnhancement.infrastructure.minecraft;

import com.google.gson.Gson;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.domain.entity.EnhancementResult;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;

import java.util.Iterator;
import java.util.List;

public class ForgingDataRepository {
    private KeyManager keyManager;
    private Gson gson;

    public ForgingDataRepository(KeyManager keyManager) {
        this.keyManager = keyManager;
        this.gson = new Gson();
    }

    public boolean isEngraved(ItemMeta itemMeta){
        return itemMeta.getPersistentDataContainer().has(keyManager.getEnhancementKey(), PersistentDataType.STRING);
    }

    public EnhancementResult getForgingData(ItemMeta itemMeta){
        String enhanceResultString = itemMeta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING);
        return gson.fromJson(enhanceResultString, EnhancementResult.class);
    }

    public ForgingAttribute getGUIForgingAttribute(ItemMeta itemMeta){
        String attributeString = itemMeta.getPersistentDataContainer().get(keyManager.getForgingAttributeGUIKey(), PersistentDataType.STRING);
        return gson.fromJson(attributeString, ForgingAttribute.class);
    }

    public List<ForgingAttribute> removeUnengravedForgingData(ItemMeta itemMeta){
        if(!isEngraved(itemMeta)) return null;
        String enhanceResultString = itemMeta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING);
        EnhancementResult enhancementResult = gson.fromJson(enhanceResultString, EnhancementResult.class);
        List<ForgingAttribute> forgingAttributes = enhancementResult.getAttributeList();
        forgingAttributes.removeIf(forgingAttribute -> !forgingAttribute.isEngraved());
        itemMeta.getPersistentDataContainer().remove(keyManager.getEnhancementKey());
        return enhancementResult.getAttributeList();
    }

    public boolean hasBaseAttributeData(ItemMeta itemMeta){
        return itemMeta.getPersistentDataContainer().has(keyManager.getBaseAttributeKey(), PersistentDataType.BYTE);
    }

    public void addBaseAttributeData(ItemMeta itemMeta){
        itemMeta.getPersistentDataContainer().set(keyManager.getBaseAttributeKey(), PersistentDataType.BYTE, (byte) 1);;
    }

    public void addForgingData(ItemMeta itemMeta, EnhancementResult enhancementResult){
        String AttributesString = gson.toJson(enhancementResult);
        itemMeta.getPersistentDataContainer().set(keyManager.getEnhancementKey(),  PersistentDataType.STRING, AttributesString);
    }

    public int getForgingItemLevel(ItemMeta itemMeta){
        EnhancementResult enhancementResult = getForgingData(itemMeta);
        return enhancementResult.getLevel();
    }
}
