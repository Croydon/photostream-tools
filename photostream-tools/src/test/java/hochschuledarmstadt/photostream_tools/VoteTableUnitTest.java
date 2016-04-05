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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class VoteTableUnitTest {

    private LikeTable voteTable;

    @Before
    public void setUp() {
        final Context context = RuntimeEnvironment.application.getApplicationContext();
        DbConnection dbTestConnectionDelegate = DbTestConnectionDelegate.getInstance(context);
        context.deleteDatabase(DbTestConnectionDelegate.DATABASE_NAME);
        voteTable = new LikeTable(dbTestConnectionDelegate);
        voteTable.openDatabase();
    }

    @After
    public void tearDown() {
        voteTable.closeDatabase();
    }

    @Test
    public void insertVote() {
        assertTrue(voteTable.like(1));
    }

    @Test
    public void hasUserLikedPhoto() {
        voteTable.like(1);
        assertTrue(voteTable.hasUserLikedPhoto(1));
    }

    @Test
    public void resetLikeForPhoto() {
        voteTable.like(1);
        assertTrue(voteTable.hasUserLikedPhoto(1));
        voteTable.resetLike(1);
        assertFalse(voteTable.hasUserLikedPhoto(1));
    }

}