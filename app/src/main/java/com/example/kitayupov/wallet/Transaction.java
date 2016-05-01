package com.example.kitayupov.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

public class Transaction implements Parcelable {

    private float amount = 0.f;
    private String type = "";
    private long date = System.currentTimeMillis();
    private boolean isProfit = false;

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        amount = in.readFloat();
        type = in.readString();
        date = in.readLong();
        isProfit = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(amount);
        dest.writeString(type);
        dest.writeLong(date);
        dest.writeByte((byte) (isProfit ? 1 : 0));
    }

    @Override
    public String toString() {
        return (isProfit ? "+" : "-") + String.valueOf(amount) + " " + type + " " + DateFormat.format("dd.MM.yyyy", date);
    }
}
