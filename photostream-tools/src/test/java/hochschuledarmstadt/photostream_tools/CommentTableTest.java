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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CommentTableTest {

    private static final String DUMMY_COMMENT_STRING = "DUMMY_COMMENT_STRING";
    private static final String SOME_ETAG_VALUE = "some etag value";
    private static final String SOME_OTHER_ETAG_VALUE = "some other etag value";
    private static final int PHOTO_ID = 1;

    private Context context;
    private CommentTable commentTable;
    DbTestConnectionDelegate dbTestDelegate;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        dbTestDelegate = new DbTestConnectionDelegate(context);
        commentTable = new CommentTable(dbTestDelegate);
        commentTable.openDatabase();
    }

    @After
    public void tearDown() {
        commentTable.closeDatabase();
        dbTestDelegate.recreateTables();
    }

    @Test
    public void insertComments(){
        long returnValue = internalInsertComments();
        assertNotEquals(-1L, returnValue);
    }

    private long internalInsertComments() {
        return commentTable.insertOrReplaceComments(PHOTO_ID, DUMMY_COMMENT_STRING, SOME_ETAG_VALUE);
    }

    @Test
    public void areNewComments(){
        internalInsertComments();
        assertTrue(commentTable.areNewComments(PHOTO_ID, SOME_OTHER_ETAG_VALUE));
    }

    @Test
    public void areNotNewComments(){
        internalInsertComments();
        assertFalse(commentTable.areNewComments(PHOTO_ID, SOME_ETAG_VALUE));
    }

    @Test
    public void loadComments(){
        internalInsertComments();
        assertEquals(DUMMY_COMMENT_STRING, commentTable.loadComments(1));
    }

    @Test
    public void createsValidDbConnection(){
        assertNotNull(DbConnection.getInstance(context));
    }

    @Test
    public void loadCommentsShouldReturnNull(){
        assertNull(commentTable.loadComments(PHOTO_ID));
    }

    @Test
    public void loadEtagShouldReturnNull(){
        assertNull(commentTable.loadEtag(PHOTO_ID));
    }

    @Test
    public void loadEtag(){
        internalInsertComments();
        assertEquals(SOME_ETAG_VALUE, commentTable.loadEtag(1));
    }


}
