package org.zzq.forgingEnhancement.domain.valueobject;

import org.zzq.forgingEnhancement.infrastructure.ForgingLogger;

import java.util.List;
import java.util.Map;

public class ForgingAttributePoolConfig {

    private List<String> forgeableEquipments;
    private Map<String, EquipmentAttribute> equipmentAttributesMap;

    public ForgingAttributePoolConfig(List<String> forgeableEquipments, Map<String, EquipmentAttribute> equipmentAttributesMap) {
        this.forgeableEquipments = forgeableEquipments;
        this.equipmentAttributesMap = equipmentAttributesMap;
    }

    public boolean isForgeableEquipment(String equipment){
        boolean forgeable = false;
        for (String forgeableEquipment : forgeableEquipments){
            if(equipment.contains(forgeableEquipment)){
                return true;
            }
        }
        return forgeable;
    }

    public EquipmentAttribute getEquipmentAllAttributes(String equipment){
        if(!isForgeableEquipment(equipment)) return null;
        String equipmentType = transferItemToType(equipment);
        return equipmentAttributesMap.get(equipmentType);
    }

    public String transferItemToType(String equipment){
        for (String forgeableEquipment : forgeableEquipments){
            if(equipment.contains(forgeableEquipment)){
                return forgeableEquipment.toLowerCase();
            }
        }
        return null;
    }
}
