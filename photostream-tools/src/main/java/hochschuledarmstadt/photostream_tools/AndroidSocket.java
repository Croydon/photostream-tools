package hochschuledarmstadt.photostream_tools;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.Photo;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Andreas Schattney on 18.02.2016.
 */
class AndroidSocket {

    public static final String NEW_PHOTO = "new_photo";
    private static final String TAG = AndroidSocket.class.getName();
    private static final String NEW_COMMENT = "new_comment";
    private static final String COMMENT_DELETED = "comment_deleted";
    private static final String PHOTO_DELETED = "photo_deleted";
    private static final String NEW_VOTE = "new_vote";

    private IO.Options options;
    private Socket socket;
    private URI uri;
    private OnMessageListener onMessageListener;

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public AndroidSocket(IO.Options options, URI uri,OnMessageListener onMessageListener) throws NoSuchAlgorithmException, KeyManagementException {
        this.options = options;
        this.uri = uri;
        this.onMessageListener = onMessageListener;
    }

    public static SSLContext createSslContext() throws KeyManagementException, NoSuchAlgorithmException {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        TrustManager tm = new X509TrustManager() {

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[]{tm}, null);

        return sslContext;
    }

    public boolean connect() throws URISyntaxException {
        if (socket != null){
            socket.off();
            socket.close();
        }
        socket = IO.socket(uri,options);
        initializeSocket();
        socket.connect();
        return socket.connected();
    }

    public boolean disconnect(){
        if (socket != null && socket.connected()){
            socket.disconnect();
        }
        return !socket.connected();
    }

    private void initializeSocket(){

        socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onMessageListener.onError();
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onMessageListener.onConnect();
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onMessageListener.onConnectError(uri);
            }
        });

        socket.on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onMessageListener.onConnectError(uri);
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onMessageListener.onDisconnect();
            }
        });

        socket.on(NEW_PHOTO,new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final Photo photo = new Gson().fromJson(args[0].toString(), Photo.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onMessageListener.onNewPhoto(photo);
                    }
                });
            }
        });

        socket.on(NEW_COMMENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                Gson gson = new Gson();
                final Comment comment = gson.fromJson(jsonObject.toString(), Comment.class);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onMessageListener.onNewComment(comment);
                    }
                });
            }
        });

        socket.on(COMMENT_DELETED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final int commentId = Integer.parseInt(args[0].toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onMessageListener.onCommentDeleted(commentId);
                    }
                });
            }
        });

        socket.on(PHOTO_DELETED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final int photoId = Integer.parseInt(args[0].toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onMessageListener.onPhotoDeleted(photoId);
                    }
                });
            }
        });

        socket.on(NEW_VOTE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    final int photoId = jsonObject.getInt("photo_id");
                    final int voteCount = jsonObject.getInt("votecount");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onMessageListener.onNewVote(photoId, voteCount);
                        }
                    });
                } catch (JSONException e) {
                    Logger.log(TAG, LogLevel.ERROR, e.toString());
                }
            }
        });

    }

    public boolean isConnected() {
        return socket != null && socket.connected();
    }

    public interface OnMessageListener{
        void onNewPhoto(Photo photo);
        void onDisconnect();
        void onConnect();
        void onError();
        void onConnectError(URI uri);
        void onNewComment(Comment comment);
        void onCommentDeleted(int commentId);
        void onPhotoDeleted(int photoId);
        void onNewVote(int photoId, int voteCount);
    }
}
