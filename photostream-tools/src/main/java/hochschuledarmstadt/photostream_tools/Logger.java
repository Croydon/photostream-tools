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

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import hochschuledarmstadt.photostream_tools.adapter.BaseAdapter;
import hochschuledarmstadt.photostream_tools.model.Photo;

class Logger {

    public static final boolean DEBUG = true;

    private static Logger logger;

    private Logger() {
    }

    public static synchronized Logger getInstance() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    public static void log(String TAG, LogLevel logLevel, String logMessage) {
        if (DEBUG) {
            logToLogcat(TAG, logLevel, logMessage);
        }

    }

    private static void logToLogcat(String tag, LogLevel logLevel, String logMessage) {
        switch (logLevel) {
            case INFO:
                Log.i(tag, logMessage);
                break;
            case ERROR:
                Log.e(tag, logMessage);
                break;
            case WARNING:
                Log.w(tag, logMessage);
                break;
        }

    }

}
