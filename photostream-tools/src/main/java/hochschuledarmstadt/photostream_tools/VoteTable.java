package hochschuledarmstadt.photostream_tools;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Andreas Schattney on 07.03.2016.
 */
class VoteTable {

    public static final String TABLE_NAME = "vote";

    public static final String COLUMN_PHOTO_ID = "channel_id";
    public static final String COLUMN_ALREADY_VOTED = "already_voted";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "("
            + COLUMN_PHOTO_ID + " INTEGER NOT NULL, "
            + COLUMN_ALREADY_VOTED + " BOOLEAN NOT NULL, "
            + String.format("PRIMARY KEY (%s)",COLUMN_PHOTO_ID)
            + ");";

    public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public VoteTable(DbConnection dbHelper) {
        this.dbHelper = dbHelper;
    }

    protected SQLiteDatabase database;
    private DbConnection dbHelper;

    public void openDatabase() {
        database = dbHelper.openDatabase();
    }

    public void closeDatabase() {
        if (database != null) {
            if (dbHelper.closeDatabase())
                database = null;
        }
    }

    public boolean insertVote(int photoId){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PHOTO_ID, photoId);
        cv.put(COLUMN_ALREADY_VOTED, true);
        return database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE) > 0;
    }

    public boolean userAlreadyVotedForPhoto(int photoId){
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{COLUMN_ALREADY_VOTED},
                String.format("%s = ?", COLUMN_PHOTO_ID),
                new String[]{String.valueOf(photoId)},
                null,null,null
        );
        boolean alreadyVoted = false;
        if (cursor.moveToFirst()){
            alreadyVoted = cursor.getInt(cursor.getColumnIndex(COLUMN_ALREADY_VOTED)) == 1;
        }
        cursor.close();
        return alreadyVoted;
    }

}
