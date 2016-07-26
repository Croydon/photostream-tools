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


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import hochschuledarmstadt.photostream_tools.model.Photo;

class HttpImageLoader implements OnResponseListener {

    private final String formatPhotoContentUrl;
    private BlockingQueue<HttpImage> blockingQueue = new ArrayBlockingQueue<>(10);
    private AtomicInteger remainingExecutors = null;

    public HttpImageLoader(String formatPhotoContentUrl){
        this.formatPhotoContentUrl = formatPhotoContentUrl;
    }

    public void execute(List<Photo> photos) {
        List<HttpRequestExecutor> httpRequestExecutors = new ArrayList<>();
        for (Photo photo : photos) {
            try {
                String u = String.format(formatPhotoContentUrl, photo.getId());
                URL url = new URL(u);
                httpRequestExecutors.add(new HttpRequestExecutor(url, photo));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        remainingExecutors = new AtomicInteger(httpRequestExecutors.size());
        new AsyncMultipleHttpRequestsExecutor(httpRequestExecutors, this).execute();
    }

    public boolean isRunning(){
        return blockingQueue.size() > 0 || remainingExecutors.get() > 0;
    }

    public HttpImage take(){
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResponse(byte[] imageData, Photo photo) {
        try {
            blockingQueue.put(new HttpImage(photo, imageData));
            remainingExecutors.decrementAndGet();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class HttpImage {
        private final byte[] imageData;
        private final Photo photo;
        HttpImage(Photo photo, byte[] imageData){
            this.photo = photo;
            this.imageData = imageData;
        }

        public Photo getPhoto() {
            return photo;
        }

        public byte[] getImageData() {
            return imageData;
        }
    }

    private static class HttpRequestExecutor implements Runnable {

        private final URL url;
        private final Photo photo;
        private OnResponseListener onResponseListener;

        public HttpRequestExecutor(URL url, Photo photo){
            this.url = url;
            this.photo = photo;
        }

        public void setOnResponseListener(OnResponseListener onResponseListener) {
            this.onResponseListener = onResponseListener;
        }

        public void execute(){
            try {
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
                byte[] data = os.toByteArray();
                os.close();
                onResponseListener.onResponse(data, photo);
                onResponseListener = null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            execute();
        }
    }

    static class AsyncMultipleHttpRequestsExecutor implements OnResponseListener {

        private final List<HttpRequestExecutor> httpRequestExecutors;
        private final ExecutorService executorService = Executors.newCachedThreadPool();
        private final OnResponseListener onResponseListenerDelegate;

        public AsyncMultipleHttpRequestsExecutor(List<HttpRequestExecutor> httpRequestExecutors, OnResponseListener onResponseListenerDelegate) {
            this.httpRequestExecutors = httpRequestExecutors;
            this.onResponseListenerDelegate = onResponseListenerDelegate;
        }

        public void execute(){
            for (HttpRequestExecutor executor : httpRequestExecutors){
                executor.setOnResponseListener(this);
                executorService.submit(executor);
            }
            executorService.shutdown();
            httpRequestExecutors.clear();

        }

        @Override
        public void onResponse(byte[] imageData, Photo photo) {
            onResponseListenerDelegate.onResponse(imageData, photo);
        }
    }


}
