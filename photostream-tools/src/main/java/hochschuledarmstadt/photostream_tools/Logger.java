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
