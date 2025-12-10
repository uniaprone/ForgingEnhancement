package org.zzq.forgingEnhancement.services.foringstone;

import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.domain.model.ForgingStone;

public interface IForgingStoneService {
    ItemStack createForgingStone(ForgingStone forgingStone, int amount);
    boolean isForgingStone(ItemStack itemStack);
    int getStoneQuality(ItemStack itemStack);
}
