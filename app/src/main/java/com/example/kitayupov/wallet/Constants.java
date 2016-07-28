package com.example.kitayupov.wallet;

import java.util.ArrayList;
import java.util.Map;

public class Constants {
    public static Map<String, Integer> profitMap;
    public static Map<String, Integer> spendMap;
    public static ArrayList<String> descriptions;

    public static void addType(boolean isProfit, String type) {
        Map<String, Integer> map = isProfit ? profitMap : spendMap;
        if (!"".equals(type)) {
            if (map.containsKey(type)) {
                map.put(type, map.get(type) + 1);
            } else {
                map.put(type, 1);
            }
        }
    }

    public static void addDescription(String desc) {
        if (!"".equals(desc) && !descriptions.contains(desc)) {
            descriptions.add(desc);
        }
    }
}
