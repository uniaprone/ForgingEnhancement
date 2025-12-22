package org.zzq.forgingEnhancement.domain.aggregateroot;

import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.domain.valueobject.*;
import org.zzq.forgingEnhancement.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelectAttribute {
    private ForgingAttributeConfig forgingAttributeConfig;
    private ForgingAttributePoolConfig forgingAttributePoolConfig;

    public SelectAttribute(ForgingAttributeConfig forgingAttributeConfig, ForgingAttributePoolConfig forgingAttributePoolConfig) {
        this.forgingAttributeConfig = forgingAttributeConfig;
        this.forgingAttributePoolConfig = forgingAttributePoolConfig;
    }

    public List<ForgingAttribute> selectAttributes(int level, String equipment){
        EquipmentAttribute equipmentAttributes = forgingAttributePoolConfig.getEquipmentAllAttributes(equipment);
        List<ForgingAttribute> commonAttributes = selectCommonAttributes(level, equipmentAttributes);
        List<ForgingAttribute> rareAttributes = selectRareAttributes(level, equipmentAttributes);
        List<ForgingAttribute> selectedAttributes = new ArrayList<>();
        selectedAttributes.addAll(commonAttributes);
        selectedAttributes.addAll(rareAttributes);
        return selectedAttributes;
    }

    private List<ForgingAttribute> selectCommonAttributes(int level, EquipmentAttribute equipmentAttributes){
        List<String> equipmentCommonAttributes = getEquipmentCommonAttribute(equipmentAttributes.getAttributes());
        int commonAttributeCount = randomCommonAttributeCount(level, equipmentCommonAttributes.size());
        List<String> commonAttributes = randomCommonAttribute(equipmentCommonAttributes, commonAttributeCount);
        return calculateAttributeQualities(commonAttributes, level);
    }

    private List<ForgingAttribute> selectRareAttributes(int level, EquipmentAttribute equipmentAttributes){
        List<ForgingAttribute> enhancements = new ArrayList<>();
        List<String> equipmentCommonAttributes = getEquipmentRareAttribute(equipmentAttributes.getAttributes());
        if (!equipmentCommonAttributes.isEmpty()) {
            for (String rareAttribute : equipmentCommonAttributes) {
                // 根据稀有概率决定是否添加稀有属性
                double randomValue = RandomUtil.nextDouble();
                if (randomValue < 0.1 + (double)(level + 1) / 30.0){// 添加稀有属性，使用最小属性值
                    double value = forgingAttributeConfig.getForgingAttributeValue(rareAttribute).getValueRangeByLevel(level).getMin();
                    enhancements.add(new ForgingAttribute(rareAttribute, level, value));
                }
            }
        }
        return enhancements;
    }

    private List<ForgingAttribute> calculateAttributeQualities(List<String> attributes, int itemQualityLevel) {
        int attributeCount = attributes.size();
        int[] attributeLevels = new int[attributeCount];
        Arrays.fill(attributeLevels, itemQualityLevel);

        double floatChance = 0.2;
        int floatableCount = (int) (attributeCount / 2.0);
        int finalFloatCount = 0;
        for (int i = 0; i < floatableCount; i++){
            if(RandomUtil.nextDouble() < floatChance){
                finalFloatCount ++;
            }
        }

        List<Integer> choseNum = new ArrayList<>();
        for(int i = 0; i < attributeCount; i++){
            choseNum.add(i);
        }
        if(choseNum.isEmpty()) return null;
        //减小
        for(int i = 0 ; i < finalFloatCount; i ++){
            int reduceNum = choseNum.get(RandomUtil.nextInt(choseNum.size()));
            attributeLevels[reduceNum] -= 1;
            choseNum.remove(Integer.valueOf(reduceNum));
        }
        //增加
        for(int i = 0 ; i < finalFloatCount; i ++){
            int addNum = choseNum.get(RandomUtil.nextInt(choseNum.size()));
            attributeLevels[addNum] += 1;
            choseNum.remove(Integer.valueOf(addNum));
        }

        List<ForgingAttribute> enhancements = new ArrayList<>();
        for (int i = 0; i < attributeCount; i++) {
            String attributeKey = attributes.get(i);
            ForgingAttributeValue forgingAttributeValue= forgingAttributeConfig.getForgingAttributeValue(attributeKey);
            double randomValue = forgingAttributeValue.getValueRangeByLevel(attributeLevels[i]).getRandomValue();
            enhancements.add(new ForgingAttribute(attributeKey, attributeLevels[i], randomValue));
        }

        return enhancements;
    }

    private List<String> randomCommonAttribute(List<String> equipmentCommonAttributes, int commonAttributeCount){
        Collections.shuffle(equipmentCommonAttributes);
        return equipmentCommonAttributes.subList(0, commonAttributeCount);
    }

    private int randomCommonAttributeCount(int level, int maxAttributes) {
        switch (level) {
            case 0, 1:
                return Math.max((int) Math.round((maxAttributes * (0.2 + RandomUtil.nextDouble() * (0.2)))), 1);
            case 2:
                return Math.max((int) Math.round((maxAttributes * (0.4 + RandomUtil.nextDouble() * (0.2)))), 1);
            case 3:
                return Math.max((int) Math.round((maxAttributes * (0.6 + RandomUtil.nextDouble() * (0.2)))), 1);
            case 4:
                return Math.max((int) Math.round((maxAttributes * (0.8 + RandomUtil.nextDouble() * (0.2)))), 1);
            case 5:
                return Math.max((int) Math.round((maxAttributes * (0.8 + RandomUtil.nextDouble() * (0.2)))), 1);
            default:
                return -1;
        }
    }

    private List<String> getEquipmentCommonAttribute(List<String> equipmentAllAttributes){
        List<String> commonAttributes = new ArrayList<>();
        for (String attribute : equipmentAllAttributes){
            if(forgingAttributeConfig.isCommonAttribute(attribute)){
                commonAttributes.add(attribute);
            }
        }
        return commonAttributes;
    }

    private List<String> getEquipmentRareAttribute(List<String> equipmentAllAttributes){
        List<String> rareAttributes = new ArrayList<>();
        for (String attribute : equipmentAllAttributes){
            if(forgingAttributeConfig.isRareAttribute(attribute)){
                rareAttributes.add(attribute);
            }
        }
        return rareAttributes;
    }
}
