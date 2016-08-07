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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class BitmapUtilsTest {

    private static final String UNIT_TEST_JPG = "unit_test.jpg";
    private static final int JPEG_QUALITY = 100;
    private static final int BITMAP_SIZE = 800;
    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConvertBitmapToBytes(){
        Bitmap bitmap = createTestBitmap();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.CYAN);
        byte[] bytes = BitmapUtils.bitmapToBytes(bitmap);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    private Bitmap createTestBitmap() {
        return Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888);
    }

    @Test
    public void testWontFailRecyclingBitmapIfPassedNull(){
        try{
            Bitmap bitmap = null;
            BitmapUtils.recycleBitmap(bitmap);
        }catch(Exception e){
            fail("no exception should be thrown");
        }
    }

    @Test
    public void testRecycleBitmap(){
        Bitmap bitmap = createTestBitmap();
        BitmapUtils.recycleBitmap(bitmap);
        assertTrue(bitmap.isRecycled());
    }

    @Test
    public void testRecycleBitmapFromImageView(){
        ImageView imageView = new ImageView(context);
        Bitmap testBitmap = createTestBitmap();
        imageView.setImageBitmap(testBitmap);
        BitmapUtils.recycleBitmapFromImageView(imageView);
        assertTrue(testBitmap.isRecycled());
        assertNull(((BitmapDrawable)imageView.getDrawable()).getBitmap());
    }
}
