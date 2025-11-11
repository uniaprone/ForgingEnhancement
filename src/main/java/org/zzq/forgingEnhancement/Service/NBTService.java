package org.zzq.forgingEnhancement.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.model.Attribute;
import org.zzq.forgingEnhancement.model.EnhancementResult;

import javax.naming.Name;
import java.util.List;

public class NBTService {
    private PluginContext pluginContext;
    private final Gson gson = new Gson();
    public NBTService(PluginContext pluginContext){
        this.pluginContext = pluginContext;
    }
    public void storeForgingNBT(ItemMeta itemMeta, NamespacedKey namespacedKey, EnhancementResult enhancementResult) {
        String AttributesString = gson.toJson(enhancementResult);
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, AttributesString);
    }
    public void markBaseAttributeApplied(ItemMeta meta) {
        meta.getPersistentDataContainer().set(pluginContext.getPlugin().getBaseAttributeKey(), PersistentDataType.BYTE, (byte) 1);
    }
    public boolean hasBaseAttributeApplied(ItemMeta meta) {
        return meta.getPersistentDataContainer().get(pluginContext.getPlugin().getBaseAttributeKey(), PersistentDataType.BYTE) != null;
    }
}
