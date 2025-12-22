package org.zzq.forgingEnhancement.services.guiService;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributeConfig;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributeValue;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI.AttributeBindingHolder;
import org.zzq.forgingEnhancement.utils.ColorUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class GUIDecorateService {
    private Logger logger;
    private final Gson gson = new Gson();
    private KeyManager keyManager;
    private ForgingAttributeConfig forgingAttributeConfig;

    private List<ItemStack> decorateItemList = List.of(
         new ItemStack(Material.GRAY_STAINED_GLASS_PANE),
         new ItemStack(Material.WHITE_STAINED_GLASS_PANE),
         new ItemStack(Material.LIME_STAINED_GLASS_PANE),
         new ItemStack(Material.PURPLE_STAINED_GLASS_PANE),
         new ItemStack(Material.YELLOW_STAINED_GLASS_PANE),
         new ItemStack(Material.PINK_STAINED_GLASS_PANE)
    );
    private int[] leftBroadSolts = new int[]{
            3,2,1,0,9,18,27,36,45,46,47,48
    };
    private int[] rightBroadSolts = new int[]{
            5,6,7,8,17,26,35,44,53,52,51,50
    };
    public GUIDecorateService(Logger logger, ForgingAttributeConfig forgingAttributeConfig, KeyManager keyManager){
        this.logger = logger;
        this.keyManager = keyManager;
        this.forgingAttributeConfig = forgingAttributeConfig;
    }

    public Map<Integer, ItemStack> placeForgingItem(ItemStack itemStack){
        Map<Integer, ItemStack> map = new HashMap<>();
        map.put(4, itemStack);
        return map;
    }

    public Map<Integer, ItemStack> itemInfoGUIBorder(int level){
        Map<Integer, ItemStack> border = new HashMap<>();
        for (int i = 0; i < leftBroadSolts.length; i++) {
            border.put(leftBroadSolts[i], decorateItemList.get(level));
            border.put(rightBroadSolts[i], decorateItemList.get(level));
            level = level - 1;
            if(level < 0) level = 5;
        }
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        Component nameComponent = Component.text("退出")
                .color(NamedTextColor.DARK_AQUA)
                .decorate(TextDecoration.BOLD);
        setCustomName(itemStack, nameComponent);
        border.put(49, itemStack);
        return border;
    }

    public void setCustomName(ItemStack itemStack, Component component) {
        if (itemStack == null) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        itemMeta.customName(component);
        itemStack.setItemMeta(itemMeta); // 必须保存修改
    }

    public Map<Integer, ItemStack> forgingAttributeSlots(List<ForgingAttribute> forgingAttributes){
        Map<Integer, ItemStack> attributeSlots = new HashMap<>();
        int slot = 20;
        for (int i = 0; i < forgingAttributes.size(); i++) {
            ItemStack paper =  new ItemStack(Material.NETHER_STAR);
            ItemMeta itemMeta = paper.getItemMeta();
            String AttributeString = gson.toJson(forgingAttributes.get(i));
            itemMeta.getPersistentDataContainer().set(keyManager.getForgingAttributeGUIKey(),  PersistentDataType.STRING, AttributeString);
            guiItemDisplay(itemMeta, forgingAttributes.get(i));
            paper.setItemMeta(itemMeta);
            attributeSlots.put(slot, paper);
            slot = slot + 1;
            if (slot > 24 && slot <= 29) {
                slot = 29;  // 从23-27跳到27
            } else if (slot > 33 && slot <= 38) {
                slot = 38;  // 从32-36跳到36
            } else if (slot > 42) {
                slot = 9;   // 超过40跳到9
            }
        }
        return attributeSlots;
    }

    public void guiItemDisplay(ItemMeta itemMeta, ForgingAttribute forgingAttribute) {
        List<Component> newLore = new ArrayList<>();
        ForgingAttributeValue config = forgingAttributeConfig.getForgingAttributeValue(forgingAttribute.getName());
        if (config != null) {
            NamedTextColor attrColor = ColorUtil.getColorByLevel(forgingAttribute.getLevel());
            String valueDisplay = formatAttributeValue(config, forgingAttribute.getValue());
            itemMeta.displayName(Component.text("属性：" + config.getName()).color(attrColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE));
            Component suffixComponent = Component.text(" 『" + getQualityDisplayName(forgingAttribute.getLevel()) + "』").color(attrColor).decorate(TextDecoration.ITALIC);
            if(forgingAttribute.getValue() > 0){
                Component attributeComponent = Component.text("+" + valueDisplay).color(attrColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE).append(suffixComponent);
                newLore.add(attributeComponent);
            }else{
                Component attributeComponent = Component.text(valueDisplay).color(attrColor).decoration(TextDecoration.ITALIC,  TextDecoration.State.FALSE).append(suffixComponent) ;
                newLore.add(attributeComponent);
            }
            if(forgingAttribute.isEngraved()){
                Component engravedLore = Component.text("已铭刻").color(NamedTextColor.DARK_GRAY);
                newLore.add(engravedLore);
            }
        }
        itemMeta.lore(newLore);
    }

    private String getQualityDisplayName(int quality) {
        switch (quality) {
            case 0: return "破损";
            case 1: return "普通";
            case 2: return "优秀";
            case 3: return "史诗";
            case 4: return "传说";
            case 5: return "神话";
            default: return "普通";
        }
    }

    private String formatAttributeValue(ForgingAttributeValue config, double value) {
        if ("ADD_NUMBER".equals(config.getOperation())) {
            return String.format("%.1f", value);
        } else {
            return String.format("%.1f%%", value * 100);
        }
    }

    public Map<Integer, ItemStack> attributeBindingSlots(AttributeBindingHolder holder, ItemStack attributeItem,int level){
        Map<Integer, ItemStack> map = new HashMap<>();
        for(int i = 0; i < 9; i++){
            if(holder.getSlotType(i).equals(AttributeBindingHolder.SlotType.DISPLAY_ONLY)){
                if(i == 1){
                    map.put(1, attributeItem);
                    continue;
                }
                map.put(i, new ItemStack(decorateItemList.get(level)));
            } else if (holder.getSlotType(i).equals(AttributeBindingHolder.SlotType.FUNCTIONAL)) {
                if(i == 6) {
                    ItemStack itemStack = new ItemStack(Material.LIME_WOOL);
                    Component nameComponent = Component.text("确认")
                                    .color(NamedTextColor.GREEN)
                                    .decorate(TextDecoration.BOLD);
                    setCustomName(itemStack, nameComponent);
                    map.put(6, itemStack);
                }
                if(i == 7) {
                    ItemStack itemStack = new ItemStack(Material.RED_WOOL);
                    Component nameComponent = Component.text("取消")
                            .color(NamedTextColor.RED)
                            .decorate(TextDecoration.BOLD);
                    setCustomName(itemStack, nameComponent);
                    map.put(7, itemStack);
                }
                if(i == 8) {
                    ItemStack itemStack = new ItemStack(Material.BARRIER);
                    Component nameComponent = Component.text("返回")
                            .color(NamedTextColor.DARK_AQUA)
                            .decorate(TextDecoration.BOLD);
                    setCustomName(itemStack, nameComponent);
                    map.put(8, itemStack);
                }
            }
        }
        return map;
    }

}
