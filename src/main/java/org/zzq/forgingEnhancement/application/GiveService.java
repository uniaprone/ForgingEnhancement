package org.zzq.forgingEnhancement.application;

import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingStone;
import org.zzq.forgingEnhancement.infrastructure.manager.EngraveStoneManager;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.ForgingStoneFactory;

public class GiveService {
    private ForgingStoneFactory forgingStoneFactory;
    private EngraveStoneManager engraveStoneManager;

    public GiveService(ForgingStoneFactory forgingStoneFactory, EngraveStoneManager engraveStoneManager) {
        this.forgingStoneFactory = forgingStoneFactory;
        this.engraveStoneManager = engraveStoneManager;
    }

    public ItemStack give(String stone, int amount, int type){
        if(type == 1){
            return forgingStoneFactory.createForgingStone(ForgingStone.valueOf(stone.toUpperCase()), amount);
        }else if(type == 2){
            return engraveStoneManager.createBindingStone(amount);
        }
        return null;
    }
}
