package com.example.kitayupov.wallet.dto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.kitayupov.wallet.Constants;

import java.util.Locale;

public class TransDbHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;

    public TransDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public TransDbHelper (Context context) {
        this(context, Constants.TRANSACTIONS_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TransTable.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format(Locale.ROOT, "drop table if exists %s", Constants.TABLE_NAME));
        onCreate(db);
    }

    public static class TransTable implements BaseColumns {
        public static final String CREATE_QUERY = String.format(Locale.ROOT,
                "create table %s (%s integer primary key autoincrement, %s real, %s text, " +
                        "%s text, %s numeric, %s numeric)",
                Constants.TABLE_NAME, _ID, Constants.AMOUNT, Constants.TYPE,
                Constants.DESCRIPTION, Constants.DATE, Constants.IS_PROFIT
        );
    }
}
