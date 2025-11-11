package org.zzq.forgingEnhancement.utils;

import java.util.Random;

public class RandomUtil {
    public static int normalDistribution(int baseLevel, int maxLevel) {
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

    public static int normalDistribution(int baseLevel, int maxLevel, double standardDeviation) {
        Random random = new Random();

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
