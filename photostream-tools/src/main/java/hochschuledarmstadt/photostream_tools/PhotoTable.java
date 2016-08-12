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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.gson.Gson;

import java.util.List;

import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;

class PhotoTable {

    public static final String TABLE_NAME = "photo";

    public static final String COLUMN_PAGE = "page";
    public static final String COLUMN_PAGE_SIZE = "page_size";
    public static final String COLUMN_PHOTOS = "photos";
    public static final String COLUMN_ETAG = "etag";

    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "("
            + COLUMN_PAGE + " INTEGER NOT NULL, "
            + COLUMN_PAGE_SIZE + " INTEGER NOT NULL, "
            + COLUMN_PHOTOS + " TEXT NOT NULL, "
            + COLUMN_ETAG + " TEXT NOT NULL, "
            + String.format("PRIMARY KEY (%s)", COLUMN_PAGE)
            + ");";

    public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    PhotoTable(DbConnection dbHelper) {
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

    public void insertOrReplacePhotos(String jsonStringPhotoQueryResult, int page, int photoPageSize, String eTag) {
        ContentValues cv = new ContentValues();
        cv.put(PhotoTable.COLUMN_PAGE, page);
        cv.put(PhotoTable.COLUMN_ETAG, eTag);
        cv.put(PhotoTable.COLUMN_PAGE_SIZE, photoPageSize);
        cv.put(PhotoTable.COLUMN_PHOTOS, jsonStringPhotoQueryResult);
        try{
            database.insertOrThrow(TABLE_NAME, null, cv);
        }catch(SQLException e){
            database.update(TABLE_NAME, cv, COLUMN_PAGE + " = ?", new String[]{String.valueOf(page)});
        }

    }

    public String loadEtagFor(int page, int photoPageSize) {
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{PhotoTable.COLUMN_ETAG},
                PhotoTable.COLUMN_PAGE + " = ? AND " + PhotoTable.COLUMN_PAGE_SIZE + " = ?",
                new String[]{String.valueOf(page), String.valueOf(photoPageSize)},
                null,null,null);

        String eTag = null;
        if (cursor.moveToFirst()){
            eTag = cursor.getString(cursor.getColumnIndex(PhotoTable.COLUMN_ETAG));
        }
        cursor.close();
        return eTag;
    }

    public PhotoQueryResult getCachedPhotoQueryResult(int page, int photoPageSize) {
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{PhotoTable.COLUMN_PHOTOS},
                PhotoTable.COLUMN_PAGE + " = ? AND " + PhotoTable.COLUMN_PAGE_SIZE + " = ?",
                new String[]{String.valueOf(page), String.valueOf(photoPageSize)},
                null,null,null);

        PhotoQueryResult photos = null;

        if (cursor.moveToFirst()){
            String json = cursor.getString(cursor.getColumnIndex(PhotoTable.COLUMN_PHOTOS));
            photos = new Gson().fromJson(json, PhotoQueryResult.class);
        }

        cursor.close();
        return photos;
    }
}
