package org.zzq.forgingEnhancement.infrastructure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.domain.model.ForgingStone;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;
import org.zzq.forgingEnhancement.utils.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ForgingStoneFactory {
    private KeyManager keyManager;
    private final Map<ForgingStone, Integer> qualityToModelDataMap = Map.of(
            ForgingStone.BROKEN, 1001,
            ForgingStone.COMMON, 1002,
            ForgingStone.UNCOMMON, 1003,
            ForgingStone.EPIC, 1004,
            ForgingStone.LEGENDARY, 1005,
            ForgingStone.MYTHIC, 1006
    );

    public ForgingStoneFactory(KeyManager keyManager){
        this.keyManager = keyManager;
    }

    public ItemStack createForgingStone(ForgingStone forgingStone, int amount){
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupName(itemMeta, forgingStone);
        setupLore(itemMeta, forgingStone);
        setNBTData(itemMeta, forgingStone);
        setupModelData(itemMeta, forgingStone);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public boolean isForgingStone(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        Byte isStone = meta.getPersistentDataContainer().get(keyManager.getForgingStoneKey(), PersistentDataType.BYTE);

        return isStone != null && isStone == 1;
    }

    public int getStoneQuality(ItemStack stone) {
        if (!isForgingStone(stone)) {
            return -1;
        }

        ItemMeta meta = stone.getItemMeta();
        Integer qualityLevel = meta.getPersistentDataContainer().get(keyManager.getStoneQualityKey(), PersistentDataType.INTEGER);

        return qualityLevel != null ? qualityLevel : -1;
    }

    private void setupModelData(ItemMeta itemMeta, ForgingStone forgingStone){
        Integer modelData = qualityToModelDataMap.get(forgingStone);
        if(modelData != null){
            itemMeta.setCustomModelData(modelData);
        }
    }
    private void setupName(ItemMeta itemMeta, ForgingStone forgingStone){
        NamedTextColor color = ColorUtil.getColorByLevel(forgingStone.getLevel());
        Component name = Component.text(forgingStone.getQuality()).color(color).append(Component.text("锻造石").color(NamedTextColor.WHITE));
        itemMeta.displayName(name);
    }

    private void setupLore(ItemMeta itemMeta, ForgingStone forgingStone){
        List<Component> lores = new ArrayList<>();
        NamedTextColor color = ColorUtil.getColorByLevel(forgingStone.getLevel());
        Component lore1 = Component.text("用于在铁砧中强化装备").color(NamedTextColor.WHITE);
        Component lore2 = Component.text("品质: " + forgingStone.getQuality()).color(color);
        lores.add(lore1);
        lores.add(lore2);
        itemMeta.lore(lores);
    }

    private void setNBTData(ItemMeta itemMeta, ForgingStone forgingStone){
        itemMeta.getPersistentDataContainer().set(keyManager.getForgingStoneKey(), PersistentDataType.BYTE, (byte) 1);
        itemMeta.getPersistentDataContainer().set(keyManager.getStoneQualityKey(), PersistentDataType.INTEGER, forgingStone.getLevel());
    }
}
