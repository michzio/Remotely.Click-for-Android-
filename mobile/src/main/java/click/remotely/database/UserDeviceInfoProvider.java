package click.remotely.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import click.remotely.model.UserDeviceInfo;

/**
 * Created by michzio on 02/08/2017.
 */

public class UserDeviceInfoProvider extends ContentProvider {

    // defining ContentProvider's URI address
    private static final String AUTHORITY = "click.remotely.provider.UserDeviceInfoProvider";
    private static final String BASE_PATH = "user_devices_info";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // defining a UriMatcher to differentiate between different URI requests:
    // for all user devices info or a single user device info
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final UriMatcher uriMatcher;

    // populating the UriMatcher object, where an URI ending
    // in 'user_devices_info' represents a request for all
    // user devices information items and 'user_devices_info/[deviceId]'
    // represents a single user device (row) information request.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, BASE_PATH, ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SINGLE_ROW);
    }

    // reference to SQLiteOpenHelper class instance
    // used to construct the underlying database.
    private DatabaseSQLiteOpenHelper databaseHelper;

    // defining the MIME types for all rows (user devices info) and single row (user device info)
    public static final String CONTENT_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.remotely.user_devices_info";
    public static final String CONTENT_ITEM_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.remotely.user_devices_info";


    @Override
    public synchronized boolean onCreate() {
        // creating instance of SQLiteOpenHelper that effectively
        // defer creating and opening a database until it is required
        databaseHelper = new DatabaseSQLiteOpenHelper(getContext());

        // returns true if the provider was successfully loaded
        return true;
    }

    /**
     * This method deletes single user device info row or set of user device info rows
     * based on selection argument depending on URI address.
     */
    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {

        // Open a read/write database to support the transaction.
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // If this is a row URI, limit the deletion to specified row
        switch (uriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1); // user device info ID
                selection = UserDeviceInfoTable.COLUMN_USER_DEVICE_INFO_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                break;
            default:
                // if URI is of ALL ROWS type than selection parameter is not modified
                break;
        }

        // To return the number of deleted items, you must specify
        // a where clause. To delete all rows and return a value pass in "1".
        if(selection == null)
            selection = "1";

        // Execute the deletion.
        int deleteCount = db.delete(UserDeviceInfoTable.TABLE_USER_DEVICE_INFO, selection, selectionArgs);

        // Notify any observers about the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    /**
     * This method is used to return the correct MIME type,
     * depending on the query type: all rows or a single row.
     */
    @Override
    public synchronized String getType(Uri uri) {

        // For a given query's Content URI we return suitable MIME type.
        switch (uriMatcher.match(uri))
        {
            case SINGLE_ROW:
                return CONTENT_ITEM_MIME_TYPE;
            case ALL_ROWS:
                return CONTENT_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /**
     * Transaction method used to insert a new row into database (represented by Content Values)
     */
    @Override
    public synchronized Uri insert(Uri uri, ContentValues values) {

        // Open a read/write database to support the transaction.
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // To add empty rows to your database by passing in an empty
        // Content Values object, you must use the null column hack
        // parameter to specify the name of the column that can be set to null.
        String nullColumnHack = null;

        long id = -1;
        // checking whether Content URI address is suitable
        switch (uriMatcher.match(uri)) {

            case ALL_ROWS:
                // insert the values into the table
                id = db.insert(UserDeviceInfoTable.TABLE_USER_DEVICE_INFO, nullColumnHack, values);
                break;
            default:
                // incorrect URI address
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(id > -1) {
            // construct and return the URI of the newly inserted row.
            Uri insertedItemUri = ContentUris.withAppendedId(CONTENT_URI, id);

            // notify any observers about the change in the data set.
            getContext().getContentResolver().notifyChange(insertedItemUri, null);

            return insertedItemUri;
        } else

        return null;
    }

    /**
     * This method enables you to perform queries on the underlying data
     * source (SQLite database) using ContentProvider. UriMatcher object
     * is used to differentiate queries for all rows and a single row.
     * SQLite Query Builder is used as a helper object for performing row-based queries.
     */
    @Override
    public synchronized Cursor query(Uri uri,
                                     String[] projection,
                                     String selection, String[] selectionArgs,
                                     String sortOrder) {

        // Open the underlying database
        SQLiteDatabase db;
        try {
            db = databaseHelper.getWritableDatabase();
        } catch(SQLException ex) {
            db = databaseHelper.getReadableDatabase();
        }

        // Replace this with valid SQL statements if necessary.
        String groupBy = null;
        String having = null;

        // Use SQLiteQueryBuilder instead of query() method
        // to simplify database query construction
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // Specify the table on which to perform query
        queryBuilder.setTables(UserDeviceInfoTable.TABLE_USER_DEVICE_INFO);

        // If this is a single row query add word ID to the base query
        switch (uriMatcher.match(uri))
        {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1); // or uri.getLastPathSegment();
                queryBuilder.appendWhere(UserDeviceInfoTable.COLUMN_USER_DEVICE_INFO_ID + "=" + rowID);
                break;
            case ALL_ROWS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);

        // set notification URI for retrieved Cursor object, to tell the cursor what URI to watch,
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // returning the result set Cursor
        return cursor;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Open a read/write database to support the transaction.
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // If this is an update of single row,
        // modify selection argument to indicate that row.
        switch(uriMatcher.match(uri)) {

            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = UserDeviceInfoTable.COLUMN_USER_DEVICE_INFO_ID + "=" + rowID
                                + (!TextUtils.isEmpty(selection)? " AND (" + selection + ")" : "");
                break;
            default:
                break;
        }

        // Perform the update.
        int updateCount = db.update(UserDeviceInfoTable.TABLE_USER_DEVICE_INFO, values, selection, selectionArgs);

        // Notify any observers about the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }

    public static class UserDeviceInfoTable {

        // Database Table
        public static final String TABLE_USER_DEVICE_INFO = "userDeviceInfoTable";
        public static final String COLUMN_USER_DEVICE_INFO_ID = "_id";
        public static final String COLUMN_DEVICE_NAME = "deviceName";
        public static final String COLUMN_HOST = "host";
        public static final String COLUMN_PORT_NUMBER = "portNumber";

        // Database Table creation - SQL Statement
        private static final String TABLE_CREATE = "create table "
                + TABLE_USER_DEVICE_INFO
                + " ("
                + COLUMN_USER_DEVICE_INFO_ID + " integer primary key autoincrement, "
                + COLUMN_DEVICE_NAME + " text not null, "
                + COLUMN_HOST + " text not null, "
                + COLUMN_PORT_NUMBER + " integer not null)";

        // called when no database exists in disk
        // and the SQLiteOpenHelper class needs to create a new one.
        public static void onCreate(SQLiteDatabase database)
        {
            // UserDeviceInfo table creation in database.
            database.execSQL(TABLE_CREATE);
        }

        // called when there is a database version mismatch
        // meaning that the version of the database on disk
        // needs to be upgraded to the current version.
        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
        {

            // Log the version upgrade
            Log.d(UserDeviceInfoTable.class.getName(),
                    "Upgrading database's UserDeviceInfo table from version " + oldVersion
                        + " to " + newVersion + ", which will destroy all old data");
            // Upgrade the existing database to conform to the new version.
            // Multiple previous versions can be handled by comparing oldVersion
            // and newVersion values.

            // Upgrade database by adding new version of UserDeviceInfo table?
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DEVICE_INFO);
            onCreate(database);
        }

        public static String addPrefix(String columnName)
        {
            return TABLE_USER_DEVICE_INFO + "." + columnName;
        }
    }
}
