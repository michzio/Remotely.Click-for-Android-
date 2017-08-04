package click.remotely.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import click.remotely.model.UserDeviceInfo;

/**
 * Created by michzio on 02/08/2017.
 */

public class DatabaseSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // method called when no database exists in disk
    // and the helper class needs to create a new one
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating tables in database:
        UserDeviceInfoProvider.UserDeviceInfoTable.onCreate(db);
    }

    // method called when upgrading the existing database
    // ex. database version has been increased
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 1) {
            // changes added in DBv2
            // ex. UserDeviceInfoProvider.UserDeviceInfoTable.onUpgrade(db, oldVersion, newVersion);
        }
        if(oldVersion < 2) {
            // changes added in DBv3
        }
    }
}
