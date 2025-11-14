package org.zzq.forgingEnhancement.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class EngraveStoneManager {
    private KeyManager keyManager;
    public EngraveStoneManager(KeyManager keyManager){
        this.keyManager = keyManager;
    }
    public ItemStack createBindingStone(int amount) {

        ItemStack stone = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta meta = stone.getItemMeta();

        if (meta == null) return stone;

        // 设置CustomModelData（客户端视觉区分）
        Integer modelData = 1100;
        meta.setCustomModelData(modelData);

        // 设置显示名称和Lore
        String displayName = "§b铭刻石";
        meta.setDisplayName(displayName);

        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§7镌刻着神秘的符文");
        meta.setLore(lore);

        // 添加NBT标签（服务器逻辑验证）
        meta.getPersistentDataContainer().set(keyManager.getEngraveStoneKey(), PersistentDataType.BYTE, (byte) 1);

        stone.setItemMeta(meta);
        return stone;
    }
    public boolean isEngraveStone(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        Byte isStone = meta.getPersistentDataContainer().get(keyManager.getForgingStoneKey(), PersistentDataType.BYTE);
        return isStone != null && isStone == 1;
    }
}
