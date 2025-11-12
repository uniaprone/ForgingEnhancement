package org.zzq.forgingEnhancement;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {
    public static void main(String[] args) {
//        final Pattern pattern = Pattern.compile("品质: 『([\\s\\S]{2})』");
        final Pattern pattern = Pattern.compile("^.*: [+\\-]\\d+\\.\\d+%? 『([\\s\\S]{2})』");
//        String a = "速度: -48.3% 『史诗』";
        String a = "&6攻击距离: +1.0 『传说』";

        // 创建Matcher对象
        Matcher matcher = pattern.matcher(a);

        // 方法1: 使用find()查找匹配
        System.out.println("=== 使用find()方法 ===");
        if (matcher.find()) {
            System.out.println("找到匹配: '" + matcher.group(0) + "'");
            System.out.println("匹配位置: " + matcher.start() + "到" + matcher.end());
        }

        // 重置Matcher以重新使用
        matcher.reset();

        // 方法2: 使用matches()检查完全匹配
        System.out.println("\n=== 使用matches()方法 ===");
        if (matcher.matches()) {
            System.out.println("完全匹配: '" + matcher.group(0) + "'");
        } else {
            System.out.println("不完全匹配");
        }

        // 方法3: 查找所有匹配（对于.*?会有多个匹配）
        System.out.println("\n=== 查找所有匹配 ===");
        matcher.reset();
        int count = 0;
        while (matcher.find()) {
            System.out.println("匹配" + (++count) + ": '" + matcher.group() +
                    "' 位置: " + matcher.start() + "-" + matcher.end());
        }
        System.out.println("总共找到 " + count + " 个匹配");
    }
}

////MultiMap
//public class Test {
//    public static void main(String[] args) {
//        Multimap<String, String> multimap = ArrayListMultimap.create();
//        multimap.put("Fruits", "Apple");
//        multimap.put("Fruits", "Banana");
//        multimap.put("Colors", "Red");
//
//        for (Map.Entry<String, String> entry : multimap.entries()) {
//            System.out.println(entry.getKey() + " -> " + entry.getValue());
//        }
//    }
//}


//public class Test {
//
//    public static void main(String[] args) {
//        int testTimes = 100;
//        List<Integer> result = new ArrayList<>();
//        for (int i = 0; i < testTimes; i++) {
//            result.add(getRandomLevel(3, 5));
//        }
//        Map<Integer,Integer> rangeMap = new HashMap<>();
//        for (int i = 0; i < result.size(); i++) {
//            if(!rangeMap.containsKey(result.get(i))){
//                rangeMap.put(result.get(i),1);
//            }
//            rangeMap.put(result.get(i),rangeMap.get(result.get(i))+1);
//        }
//        for (Map.Entry<Integer, Integer> entry : rangeMap.entrySet()) {
//            System.out.println("等级" + entry.getKey()+ "出现概率：" + (double) entry.getValue() / testTimes + "%");
//        }
//    }
//    private static int  getRandomLevel(int baseLevel, int maxLevel) {
//        Random random = new Random();
//
//        // 使用正态分布，均值为基础品质等级，标准差为1.0
//        double standardDeviation = 0.4;
//
//        double gaussianValue;
//        int newLevel;
//
//        // 使用截断正态分布，确保结果在合理范围内
//        do {
//            gaussianValue = random.nextGaussian();
//            double adjustedValue = (double) baseLevel + standardDeviation * gaussianValue;
//            newLevel = (int) Math.round(adjustedValue);
//        } while (newLevel < 0 || newLevel > maxLevel);
//
//        return newLevel;
//    }
//}
