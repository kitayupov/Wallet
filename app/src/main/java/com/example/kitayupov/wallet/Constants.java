package com.example.kitayupov.wallet;

import java.util.ArrayList;

public class Constants {
    public static ArrayList<String> categories;
    public static ArrayList<String> descriptions;

    public static void addCategory(String type) {
        if (!"".equals(type) && !Constants.categories.contains(type)) {
            Constants.categories.add(type);
        }
    }

    public static void addDescription(String desc) {
        if (!"".equals(desc) && !Constants.categories.contains(desc)) {
            Constants.categories.add(desc);
        }
    }
}
