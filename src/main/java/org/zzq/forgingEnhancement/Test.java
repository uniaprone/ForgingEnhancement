package org.zzq.forgingEnhancement;

import net.kyori.adventure.text.event.ClickEvent;

import java.util.*;

public class Test {

    public static void main(String[] args) {
        int testTimes = 100;
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < testTimes; i++) {
            result.add(getRandomLevel(3, 5));
        }
        Map<Integer,Integer> rangeMap = new HashMap<>();
        for (int i = 0; i < result.size(); i++) {
            if(!rangeMap.containsKey(result.get(i))){
                rangeMap.put(result.get(i),1);
            }
            rangeMap.put(result.get(i),rangeMap.get(result.get(i))+1);
        }
        for (Map.Entry<Integer, Integer> entry : rangeMap.entrySet()) {
            System.out.println("等级" + entry.getKey()+ "出现概率：" + (double) entry.getValue() / testTimes + "%");
        }
    }
    private static int  getRandomLevel(int baseLevel, int maxLevel) {
        Random random = new Random();

        // 使用正态分布，均值为基础品质等级，标准差为1.0
        double standardDeviation = 0.4;

        double gaussianValue;
        int newLevel;

        // 使用截断正态分布，确保结果在合理范围内
        do {
            gaussianValue = random.nextGaussian();
            double adjustedValue = (double) baseLevel + standardDeviation * gaussianValue;
            newLevel = (int) Math.round(adjustedValue);
        } while (newLevel < 0 || newLevel > maxLevel);

        return newLevel;
    }


}
