package hochschuledarmstadt.photostream_tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Andreas Schattney on 07.03.2016.
 */
class DbConnection extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "photostream";
    private static final int DATABASE_VERSION = 1;

    private static DbConnection instance;
    protected SQLiteDatabase database;
    private int openConnections = 0;

    protected DbConnection(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(VoteTable.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized SQLiteDatabase openDatabase() {

        synchronized (this) {

            if (database == null) {
                database = getWritableDatabase();
            }
            openConnections++;
            return database;

        }
    }

    public synchronized boolean closeDatabase() {

        synchronized (this) {

            if (openConnections > 0)
                openConnections--;

            if (database != null && openConnections == 0) {
                database.close();
                database = null;
                return true;
            }

            return false;

        }
    }

    public static synchronized DbConnection getInstance(Context context) {
        if (instance == null)
            instance = new DbConnection(context, DATABASE_NAME, null, DATABASE_VERSION);
        return instance;
    }
}
