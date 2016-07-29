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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import hochschuledarmstadt.photostream_tools.model.Comment;

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
        db.execSQL(CommentTable.TABLE_CREATE);
        db.execSQL(PhotoTable.TABLE_CREATE);
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
