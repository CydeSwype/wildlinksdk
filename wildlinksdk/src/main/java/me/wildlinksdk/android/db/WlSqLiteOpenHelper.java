package me.wildlinksdk.android.db;

import android.content.Context;
import android.util.Log;
import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;

public class WlSqLiteOpenHelper extends SQLiteOpenHelper {
    public static final String TABLE_CONCEPTS = "concepts";
    public static final String TABLE_MERCHANTS = "merchants";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DOMAIN = "domain";
    public static final String COLUMN_TOKEN = "token";

    private static final String TAG = WlSqLiteOpenHelper.class.getSimpleName();
    private static final String CREATE_MERCHANTS_TABLE = "CREATE TABLE "
        + TABLE_MERCHANTS
        + " ("
        + COLUMN_ID
        + " TEXT NOT NULL  , "
        + COLUMN_DOMAIN
        + " text  NOT NULL);";
    private static final String CREATE_CONCEPTS_TABLE = "CREATE VIRTUAL TABLE "
        + TABLE_CONCEPTS
        + " USING fts5("
        + COLUMN_ID
        + "  UNINDEXED, "
        + COLUMN_TOKEN
        + " ,tokenize = \"unicode61 remove_diacritics 1 tokenchars '.-'\");";
    private Context context;

    public WlSqLiteOpenHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(WlSqLiteOpenHelper.class.getName(), "Upgrading database from version "
            + oldVersion
            + " to "
            + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONCEPTS);
        Log.d(WlSqLiteOpenHelper.class.getName(), "Upgrading database from version "
            + oldVersion
            + " to "
            + newVersion
            + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MERCHANTS);
        onCreate(db);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        try {

            Log.d(TAG, "onCreate creating table");

            db.execSQL(CREATE_MERCHANTS_TABLE);
            db.execSQL(CREATE_CONCEPTS_TABLE);

            Log.d(TAG, "created tables");
        } catch (Exception e) {
            Log.v(TAG, e.getMessage());
        }
    }
}

