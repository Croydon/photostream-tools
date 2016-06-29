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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        Bitmap bitmap = null;
        BitmapUtils.recycleBitmap(bitmap);
    }

    @Test
    public void testRecycleBitmap(){
        Bitmap bitmap = createTestBitmap();
        BitmapUtils.recycleBitmap(bitmap);
    }

    @Test
    public void testRecycleBitmapFromImageView(){
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(createTestBitmap());
        BitmapUtils.recycleBitmapFromImageView(imageView);
        assertTrue(((BitmapDrawable)imageView.getDrawable()).getBitmap().isRecycled());
    }

    @Test
    public void testReadBitmapFromFile(){
        Bitmap bitmap = createTestBitmap();
        try {
            FileOutputStream fs = context.openFileOutput(UNIT_TEST_JPG, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fs);
            File file = new File(context.getFilesDir(), UNIT_TEST_JPG);
            Bitmap result = BitmapUtils.decodeBitmapFromFile(context, file);
            if (file.exists())
                file.delete();
            assertNotNull(result);
            assertTrue(!result.isRecycled());
        } catch (FileNotFoundException e) {
            assertFalse(e.toString(), true);
        }
    }

    @Test
    public void testReadBitmapFromUri(){

        Bitmap bitmap = createTestBitmap();

        try {

            FileOutputStream fs = context.openFileOutput(UNIT_TEST_JPG, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fs);

            File file = new File(context.getFilesDir(), UNIT_TEST_JPG);
            Bitmap result = BitmapUtils.decodeBitmapFromUri(context, Uri.fromFile(file));
            if (file.exists())
                file.delete();
            assertNotNull(result);
            assertTrue(!result.isRecycled());
            result.recycle();
        } catch (FileNotFoundException e) {
            assertFalse(e.toString(), true);
        }

    }
}
