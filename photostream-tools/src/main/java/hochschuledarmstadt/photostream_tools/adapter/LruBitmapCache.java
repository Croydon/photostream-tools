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

package hochschuledarmstadt.photostream_tools.adapter;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


class LruBitmapCache extends android.support.v4.util.LruCache<Integer, Bitmap> {

    private HashMap<Integer, Integer> hitCountMap = new HashMap<>();

    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    private HashMap<Integer, Bitmap> removedBitmaps = new HashMap<>();

    public synchronized void referenceIncrease(@NonNull Integer key) {
        boolean containsKey = hitCountMap.containsKey(key);
        Bitmap bitmap = this.get(key);
        if (bitmap != null) {
            int value = 0;
            if (containsKey)
                value = hitCountMap.get(key);
            hitCountMap.put(key, ++value);
        } else {
            if (containsKey)
                hitCountMap.remove(key);
        }
    }

    public synchronized void referenceDecrease(@NonNull Integer key) {
        boolean containsKey = hitCountMap.containsKey(key);
        Bitmap bitmap = this.get(key);
        if (bitmap != null) {
            int value = 0;
            if (containsKey)
                value = hitCountMap.get(key);
            if (value > 1)
                hitCountMap.put(key, --value);
            else {
                if (containsKey)
                    hitCountMap.remove(key);
            }
        } else {
            if (containsKey)
                hitCountMap.remove(key);
            if (removedBitmaps.containsKey(key)){
                bitmap = removedBitmaps.get(key);
                if (bitmap != null)
                    bitmap.recycle();
                removedBitmaps.remove(key);
                Log.d(BasePhotoAdapter.class.getName(), "PREVIOUSLY REMOVED bitmap RECYCLED while REF DECREASE for photo with id: " + key);
            }
        }
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return value.getAllocationByteCount() / 1024;
        } else {
            return value.getByteCount() / 1024;
        }
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        if (removedBitmaps.containsKey(key)) {
            removedBitmaps.get(key).recycle();
            removedBitmaps.remove(key);
            Log.d(BasePhotoAdapter.class.getName(), "PREVIOUSLY removed bitmap RECYCLED for photo with id: " + key);
        }
        int refCount = 0;
        if (hitCountMap.containsKey(key)) {
            refCount = hitCountMap.get(key);
        }
        if (refCount == 0) {
            if (!oldValue.isRecycled())
                oldValue.recycle();
            Log.d(BasePhotoAdapter.class.getName(), "Bitmap REMOVED and RECYCLED for photo with id: " + key);
        } else {
            if (hitCountMap.containsKey(key))
                hitCountMap.remove(key);
            removedBitmaps.put(key, oldValue);
            Log.d(BasePhotoAdapter.class.getName(), "Bitmap REMOVED for photo with id: " + key);
        }
        super.entryRemoved(evicted, key, oldValue, newValue);
    }

    public void destroy() {
        hitCountMap.clear();
        Collection<Bitmap> values = removedBitmaps.values();
        for (Bitmap b : values){
            if (!b.isRecycled())
                b.recycle();
        }
        removedBitmaps.clear();
        evictAll();
    }

}
