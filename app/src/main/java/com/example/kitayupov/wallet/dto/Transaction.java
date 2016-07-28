package com.example.kitayupov.wallet.dto;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

public class Transaction implements Parcelable {

    private float amount = 0.f;
    private String type = "";
    private String description = "";
    private long date = System.currentTimeMillis();
    private boolean isProfit = false;

    public Transaction(float amount, String type, String description, long date, boolean isProfit) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
        this.isProfit = isProfit;
    }

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        setAmount(in.readFloat());
        setType(in.readString());
        setDescription(in.readString());
        setDate(in.readLong());
        setProfit(in.readByte() != 0);
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(getAmount());
        dest.writeString(getType());
        dest.writeString(getDescription());
        dest.writeLong(getDate());
        dest.writeByte((byte) (isProfit() ? 1 : 0));
    }

    @Override
    public String toString() {
        return (isProfit() ? "+" : "-") + String.valueOf(getAmount()) + " " + getType() + " " +
                getDescription() + " " + DateFormat.format("dd.MM.yyyy", getDate());
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isProfit() {
        return isProfit;
    }

    public void setProfit(boolean profit) {
        isProfit = profit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
