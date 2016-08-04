package com.example.kitayupov.wallet.dto;

public class StatisticsItem {

    private String type = "";
    private float amount = 0f;
    private float percent = 0f;

    public StatisticsItem(String name, float amount, float percent) {
        this.type = name;
        this.amount = amount;
        this.percent = percent;
    }

    public String getType() {
        return type;
    }

    public float getAmount() {
        return amount;
    }

    public float getPercent() {
        return percent;
    }
}
