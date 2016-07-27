package com.example.kitayupov.wallet;

import java.util.ArrayList;
import java.util.Map;

public class Constants {
    public static Map<String, Integer> categories;
    public static ArrayList<String> descriptions;

    public static void addCategory(String type) {
        if (!"".equals(type)) {
            if (categories.containsKey(type)) {
                categories.put(type, categories.get(type) + 1);
            } else {
                categories.put(type, 1);
            }
        }
    }

    public static void addDescription(String desc) {
        if (!"".equals(desc) && !descriptions.contains(desc)) {
            descriptions.add(desc);
        }
    }
}
