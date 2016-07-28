package com.example.kitayupov.wallet;

import java.util.Map;

public class Constants {
    public static Map<String, Integer> profitMap;
    public static Map<String, Integer> spendMap;
    public static Map<String, Integer> descriptionMap;

    public static void addType(boolean isProfit, String type) {
        Map<String, Integer> map = isProfit ? profitMap : spendMap;
        addType(map, type);
    }

    public static void addType(Map<String, Integer> map, String type) {
        if (!"".equals(type)) {
            if (map.containsKey(type)) {
                map.put(type, map.get(type) + 1);
            } else {
                map.put(type, 1);
            }
        }
    }

    public static void addTypeAmount(Map<String, Float> map, String type, Float amount) {
        if (!"".equals(type)) {
            if (map.containsKey(type)) {
                map.put(type, map.get(type) + amount);
            } else {
                map.put(type, amount);
            }
        }
    }

    public static void addDescription(String desc) {
        if (!"".equals(desc)) {
            if (descriptionMap.containsKey(desc)) {
                descriptionMap.put(desc, descriptionMap.get(desc) + 1);
            } else {
                descriptionMap.put(desc, 1);
            }
        }
    }
}
