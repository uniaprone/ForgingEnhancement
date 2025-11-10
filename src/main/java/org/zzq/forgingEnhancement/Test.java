package org.zzq.forgingEnhancement;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
        Random random = new Random();
        double randomValue = 0.2 + random.nextDouble() * (0.2);
        System.out.println("生成的随机数: " + randomValue);
    }
}
