package org.zzq.forgingEnhancement.services.foringstone;

import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingStone;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.ForgingStoneFactory;

public class ForgingStoneServiceImpl implements IForgingStoneService{
    private ForgingStoneFactory forgingStoneFactory;

    public ForgingStoneServiceImpl(ForgingStoneFactory forgingStoneFactory) {
        this.forgingStoneFactory = forgingStoneFactory;
    }

    @Override
    public ItemStack createForgingStone(ForgingStone forgingStone, int amount) {
        if(forgingStone == null) return null;
        if(amount < 1) amount = 1;
        return forgingStoneFactory.createForgingStone(forgingStone, amount);
    }

    @Override
    public boolean isForgingStone(ItemStack itemStack) {
        if(itemStack == null) return false;
        return forgingStoneFactory.isForgingStone(itemStack);
    }

    @Override
    public int getStoneQuality(ItemStack itemStack) {
        return forgingStoneFactory.getStoneQuality(itemStack);
    }
}
