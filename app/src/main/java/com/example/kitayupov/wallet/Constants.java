package com.example.kitayupov.wallet;

import java.util.Map;

public class Constants {

    public static final int REQUEST_CODE = 200;

    public static final String POSITION = "position";
    public static final String AMOUNT = "amount";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String DATE = "date";
    public static final String IS_PROFIT = "is_profit";

    public static final String TRANSACTIONS_DB = "transactions.db";
    public static final String TABLE_NAME = "Transactions";

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
        if (map.containsKey(type)) {
            map.put(type, map.get(type) + amount);
        } else {
            map.put(type, amount);
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
