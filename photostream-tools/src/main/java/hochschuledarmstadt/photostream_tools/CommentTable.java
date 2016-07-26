/*
 * The MIT License
 *
 * Copyright (c) 2016 Andreas Schattney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hochschuledarmstadt.photostream_tools;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class CommentTable {
    public static final String TABLE_NAME = "comment";

    public static final String COLUMN_PHOTO_ID = "photo_id";
    public static final String COLUMN_COMMENTS = "comments";
    public static final String COLUMN_ETAG = "etag";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "("
            + COLUMN_PHOTO_ID + " INTEGER NOT NULL, "
            + COLUMN_COMMENTS + " TEXT NOT NULL, "
            + COLUMN_ETAG + " TEXT NOT NULL, "
            + String.format("PRIMARY KEY (%s)",COLUMN_PHOTO_ID)
            + ");";

    public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public CommentTable(DbConnection dbHelper) {
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

    public boolean areNewComments(int photoId, String etag){
        String storedEtag = this.loadEtag(photoId);
        return etag != null && !etag.equals(storedEtag);
    }

    public long insertOrReplaceComments(int photoId, String comments, String etag){

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PHOTO_ID, photoId);
        cv.put(COLUMN_COMMENTS, comments);
        cv.put(COLUMN_ETAG, etag);

        return database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String loadEtag(int photoId) {
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{COLUMN_ETAG},
                String.format("%s = ?", COLUMN_PHOTO_ID),
                new String[]{String.valueOf(photoId)},
                null,null,null
        );
        String storedEtag = null;
        if (cursor.moveToFirst()){
            storedEtag = cursor.getString(cursor.getColumnIndex(COLUMN_ETAG));
        }
        cursor.close();
        return storedEtag;
    }

    public String loadComments(int photoId) {
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{COLUMN_COMMENTS},
                String.format("%s = ?", COLUMN_PHOTO_ID),
                new String[]{String.valueOf(photoId)},
                null,null,null
        );
        String comments = null;
        if (cursor.moveToFirst()){
            comments = cursor.getString(cursor.getColumnIndex(COLUMN_COMMENTS));
        }
        cursor.close();
        return comments;
    }
}
