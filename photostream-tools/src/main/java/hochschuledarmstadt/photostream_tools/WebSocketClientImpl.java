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

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.socket.client.IO;
import io.socket.engineio.client.transports.WebSocket;

class WebSocketClientImpl implements WebSocketClient {

    public static final int RECONNECTION_DELAY_IN_MILLIS = 3000;
    public static final int COUNT_OF_RECONNECTION_ATTEMPTS = 30;
    private String installationId;
    private final HttpImageLoader imageLoader;
    private String url;
    private AndroidSocket.OnMessageListener messageListener;
    private AndroidSocket androidSocket;
    private ImageCacher imageCacher;

    public WebSocketClientImpl(String url, String installationId, ImageCacher imageCacher, HttpImageLoader imageLoader) {
        this.url = url;
        this.installationId = installationId;
        this.imageCacher = imageCacher;
        this.imageLoader = imageLoader;
    }

    @Override
    public void setMessageListener(AndroidSocket.OnMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public boolean connect() {
        try {
            if (androidSocket == null){
                IO.Options options = new IO.Options();
                options.reconnectionDelay = RECONNECTION_DELAY_IN_MILLIS;
                options.reconnection = true;
                options.transports = new String[]{WebSocket.NAME};
                options.reconnectionAttempts = COUNT_OF_RECONNECTION_ATTEMPTS;
                options.rememberUpgrade = true;
                String endpoint = String.format("%s/?token=%s", this.url, installationId);
                URI uri = URI.create(endpoint);
                androidSocket = new AndroidSocket(options, uri, imageLoader, imageCacher, messageListener);
            }
            return androidSocket.connect();
        } catch (KeyManagementException e) {
            Log.e(PhotoStreamService.class.getName(), e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e(PhotoStreamService.class.getName(), e.toString());
        } catch (URISyntaxException e) {
            Log.e(PhotoStreamService.class.getName(), e.toString());
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return androidSocket.isConnected();
    }

    @Override
    public void destroy() {
        androidSocket.destroy();
    }
}
