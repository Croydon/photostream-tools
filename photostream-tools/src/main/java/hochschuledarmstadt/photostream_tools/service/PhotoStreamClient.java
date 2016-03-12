package hochschuledarmstadt.photostream_tools.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hochschuledarmstadt.photostream_tools.database.DbConnection;
import hochschuledarmstadt.photostream_tools.database.VoteTable;
import hochschuledarmstadt.photostream_tools.log.LogLevel;
import hochschuledarmstadt.photostream_tools.log.Logger;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;
import hochschuledarmstadt.photostream_tools.model.StoreCommentQuery;
import io.socket.client.IO;
import io.socket.engineio.client.transports.WebSocket;

/**
 * Created by Andreas Schattney on 08.03.2016.
 */
public class PhotoStreamClient implements AndroidSocket.OnMessageListener{

    public static final String INTENT_NEW_PHOTO = "hochschuledarmstadt.photostream_tools.intent.NEW_PHOTO";
    private final String installationId;
    private final Context context;
    private DbConnection dbConnection;

    private static final Handler handler = new Handler(Looper.getMainLooper());
    private String lastQuery = "";

    public PhotoStreamClient(Context context,DbConnection dbConnection, String installationId){
        this.context = context;
        this.dbConnection = dbConnection;
        this.installationId = installationId;
    }

    private static final String EXTERNAL_URI = "http://5.45.97.155:8081";
    private static final String TAG = PhotoStreamService.class.getName();
    private static final int NOTIFICATION_ID_NEW_PHOTO = 23798;

    private ArrayList<OnPhotosResultListener> onPhotosResultListeners = new ArrayList<>();
    private ArrayList<ServiceApiConnectionListener> serviceApiConnectionListeners = new ArrayList<>();
    private ArrayList<OnPopularPhotosResultListener> onPopularPhotosResultListeners = new ArrayList<>();
    private ArrayList<OnPhotoVotedResultListener> onPhotoVotedResultListeners = new ArrayList<>();
    private ArrayList<OnCommentsResultListener> onCommentsResultListeners = new ArrayList<>();
    private ArrayList<OnPhotoUploadListener> onPhotoUploadListeners = new ArrayList<>();
    private ArrayList<OnSearchPhotosResultListener> onSearchPhotosListeners = new ArrayList<>();

    private WebSocketClient webSocketClient;

    public void destroy() {
        openRequests.clear();
        if (webSocketClient != null)
            webSocketClient.disconnect();
    }

    private final HashMap<RequestType, Integer> openRequests = new HashMap<>();

    public void addOnPhotoVotedResultListener(OnPhotoVotedResultListener onPhotoVotedResultListener) {
        if (!onPhotoVotedResultListeners.contains(onPhotoVotedResultListener))
            onPhotoVotedResultListeners.add(onPhotoVotedResultListener);
    }

    public void removeOnPhotoVotedResultListener(OnPhotoVotedResultListener onPhotoVotedResultListener) {
        if (onPhotoVotedResultListeners.contains(onPhotoVotedResultListener))
            onPhotoVotedResultListeners.remove(onPhotoVotedResultListener);
    }

    public void addOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener){
        if (!onCommentsResultListeners.contains(onCommentsResultListener))
            onCommentsResultListeners.add(onCommentsResultListener);
    }

    public void removeOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener){
        if (onCommentsResultListeners.contains(onCommentsResultListener))
            onCommentsResultListeners.remove(onCommentsResultListener);
    }

    public void addOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener) {
        if (!onPhotosResultListeners.contains(onPhotosResultListener))
            onPhotosResultListeners.add(onPhotosResultListener);
    }

    public void removeOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener){
        if (onPhotosResultListeners.contains(onPhotosResultListener))
            onPhotosResultListeners.remove(onPhotosResultListener);
    }

    public void addOnPopularPhotosResultListener(OnPopularPhotosResultListener onPopularPhotosResultListener) {
        if (!onPopularPhotosResultListeners.contains(onPopularPhotosResultListener))
            onPopularPhotosResultListeners.add(onPopularPhotosResultListener);
    }

    public void removeOnPopularPhotosResultListener(OnPopularPhotosResultListener onPopularPhotosResultListener){
        if (onPopularPhotosResultListeners.contains(onPopularPhotosResultListener))
            onPopularPhotosResultListeners.remove(onPopularPhotosResultListener);
    }

    public void addServiceApiConnectionListener(ServiceApiConnectionListener listener){
        if (!serviceApiConnectionListeners.contains(listener))
            serviceApiConnectionListeners.add(listener);
    }

    public void removeServiceApiConnectionListener(ServiceApiConnectionListener listener){
        if (serviceApiConnectionListeners.contains(listener))
            serviceApiConnectionListeners.remove(listener);
    }

    public void addOnSearchPhotosResultListener(OnSearchPhotosResultListener listener){
        if (!onSearchPhotosListeners.contains(listener))
            onSearchPhotosListeners.add(listener);
    }

    public void removeOnSearchPhotosResultListener(OnSearchPhotosResultListener listener){
        if (onSearchPhotosListeners.contains(listener))
            onSearchPhotosListeners.remove(listener);
    }

    public void bootstrap(){
        webSocketClient = new WebSocketClient(EXTERNAL_URI, installationId, this);
        webSocketClient.connect();
    }

    public void getPhotos(int page) {
        final RequestType requestType = RequestType.PHOTOS;
        GetStreamAsyncTask task = new GetStreamAsyncTask(context, installationId, EXTERNAL_URI, page, new GetStreamAsyncTask.StreamCallback() {
            @Override
            public void onStreamQueryResult(PhotoQueryResult queryResult) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotos(queryResult);
            }

            @Override
            public void onError() {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotosFailed();
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute();
    }

    private void addOpenRequest(RequestType requestType) {
        synchronized (openRequests) {
            int n = 1;
            if (openRequests.containsKey(requestType)) {
                n = openRequests.get(requestType);
                n++;
                openRequests.remove(requestType);
            }
            openRequests.put(requestType, n);
        }
    }

    private int getOpenRequest(RequestType requestType){
        if (openRequests.containsKey(requestType)) {
            return openRequests.get(requestType);
        }
        return 0;
    }

    private void removeOpenRequest(RequestType requestType){
        synchronized (openRequests) {
            if (openRequests.containsKey(requestType)) {
                int amount = openRequests.get(requestType);
                amount--;
                openRequests.remove(requestType);
                if (amount > 0)
                    openRequests.put(requestType, amount);
            }
        }
    }

    private void determineShouldDismissProgressDialog(RequestType requestType) {
        synchronized (openRequests) {
            int amount = getOpenRequest(requestType);
            if (amount == 0)
                notifyDismissProgressDialog(requestType);
        }
    }

    private void determineShouldShowProgressDialog(RequestType requestType) {
        synchronized (openRequests) {
            int amount = getOpenRequest(requestType);
            if (amount == 1)
                notifyShowProgressDialog(requestType);
        }
    }

    private void notifyShowProgressDialog(RequestType requestType) {
        ArrayList<? extends OnRequestListener> onRequestListeners = null;
        switch(requestType){
            case PHOTOS:
                onRequestListeners = onPhotosResultListeners;
                break;
            case COMMENT:
                onRequestListeners = onCommentsResultListeners;
                break;
            case VOTE:
                onRequestListeners = onPhotoVotedResultListeners;
                break;
            case POPULAR_PHOTOS:
                onRequestListeners = onPopularPhotosResultListeners;
                break;
            case UPLOAD:
                onRequestListeners = onPhotoUploadListeners;
                break;
            case SEARCH:
                onRequestListeners = onSearchPhotosListeners;
                break;
        }
        for (OnRequestListener onRequestListener : onRequestListeners){
            onRequestListener.onShowProgressDialog();
        }
    }

    private void notifyDismissProgressDialog(final RequestType requestType) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<? extends OnRequestListener> onRequestListeners = null;
                switch(requestType){
                    case PHOTOS:
                        onRequestListeners = onPhotosResultListeners;
                        break;
                    case COMMENT:
                        onRequestListeners = onCommentsResultListeners;
                        break;
                    case VOTE:
                        onRequestListeners = onPhotoVotedResultListeners;
                        break;
                    case POPULAR_PHOTOS:
                        onRequestListeners = onPopularPhotosResultListeners;
                        break;
                    case UPLOAD:
                        onRequestListeners = onPhotoUploadListeners;
                        break;
                    case SEARCH:
                        onRequestListeners = onSearchPhotosListeners;
                }
                for (OnRequestListener onRequestListener : onRequestListeners){
                    onRequestListener.onDismissProgressDialog();
                }
            }
        }, 500);
    }

    private void notifyOnPhotosFailed() {
        for (OnPhotosResultListener resultListener : onPhotosResultListeners)
            resultListener.onReceivePhotosFailed();
    }

    public void getPopularPhotos(int page){
        final RequestType requestType = RequestType.POPULAR_PHOTOS;
        GetPopularPhotosAsyncTask task = new GetPopularPhotosAsyncTask(context, installationId, EXTERNAL_URI, page, new GetPopularPhotosAsyncTask.StreamCallback() {
            @Override
            public void onStreamQueryResult(PhotoQueryResult queryResult) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPopularPhotos(queryResult);
            }

            @Override
            public void onError() {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPopularPhotosFailed();
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute();
    }

    private void notifyOnPopularPhotosFailed() {
        for (OnPopularPhotosResultListener onPopularPhotosResultListener : onPopularPhotosResultListeners){
            onPopularPhotosResultListener.onReceivePopularPhotosFailed();
        }
    }

    private void notifyOnPhotos(PhotoQueryResult photoQueryResult) {
        for (OnPhotosResultListener onPhotosResultListener : onPhotosResultListeners){
            onPhotosResultListener.onPhotosReceived(photoQueryResult);
        }
    }

    private void notifyOnPopularPhotos(PhotoQueryResult photoQueryResult) {
        for (OnPopularPhotosResultListener onPopularPhotosResultListener : onPopularPhotosResultListeners){
            onPopularPhotosResultListener.onPopularPhotosReceived(photoQueryResult);
        }
    }

    private void notifyOnNewPhoto(Photo photo) {
        if (areListenersRegisteredToNotify()) {
            for (OnPhotoListener onPhotosResultListener : onPhotosResultListeners)
                onPhotosResultListener.onNewPhoto(photo);
            for (OnPhotoListener onPhotosResultListener : onPopularPhotosResultListeners)
                onPhotosResultListener.onNewPhoto(photo);
        }else if(photoIsNotFromThisUser(photo)){
            context.sendBroadcast(new Intent(INTENT_NEW_PHOTO));
        }
    }

    private boolean photoIsNotFromThisUser(Photo photo) {
        return !photo.isDeleteable();
    }

    private boolean areListenersRegisteredToNotify() {
        return onPhotosResultListeners.size() > 0 || onPopularPhotosResultListeners.size() > 0;
    }

    private void notifyOnPhotoVoted(int photoId, int newVoteCount) {
        for (OnPhotoVotedResultListener onPhotoVotedResultListener : onPhotoVotedResultListeners){
            onPhotoVotedResultListener.onPhotoVoted(photoId, newVoteCount);
        }
    }

    public void upvotePhoto(int photoId) {
        final RequestType requestType = RequestType.VOTE;
        VoteTable voteTable = new VoteTable(DbConnection.getInstance(context));
        VotePhotoAsyncTask task = new UpvotePhotoAsyncTask(voteTable, installationId, EXTERNAL_URI, photoId, new VotePhotoAsyncTask.OnVotePhotoResultListener() {
            @Override
            public void onPhotoVoted(int photoId, int newVoteCount) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotoVoted(photoId, newVoteCount);
            }

            @Override
            public void onPhotoVoteFailed(int photoId) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotoVoteFailed(photoId);
            }

            @Override
            public void onError(int photoId, Exception e) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
            }

            @Override
            public void onPhotoAlreadyVoted(int photo_id, int votecount) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotoAlreadyVoted(photo_id, votecount);
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute();
    }

    private void notifyOnPhotoVoteFailed(int photoId) {
        for (OnPhotoVotedResultListener listener : onPhotoVotedResultListeners)
            listener.onPhotoVoteFailed(photoId);
    }

    private void notifyOnPhotoAlreadyVoted(int photo_id, int votecount) {
        for (OnPhotoVotedResultListener listener : onPhotoVotedResultListeners)
            listener.onPhotoVoted(photo_id, votecount);
    }

    public boolean userAlreadyVotedForPhoto(int photoId){
        VoteTable voteTable = new VoteTable(dbConnection);
        voteTable.openDatabase();
        boolean alreadyVoted = voteTable.userAlreadyVotedForPhoto(photoId);
        voteTable.closeDatabase();
        return alreadyVoted;
    }

    public void getComments(int photoId){
        final RequestType requestType = RequestType.COMMENT;
        GetCommentsAsyncTask task = new GetCommentsAsyncTask(installationId, EXTERNAL_URI, photoId, new GetCommentsAsyncTask.OnCommentsResultListener() {

            @Override
            public void onGetComments(int photoId, List<Comment> comments) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnComments(photoId, comments);
            }

            @Override
            public void onGetCommentsFailed() {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute();
    }

    private void notifyOnComments(int photoId, List<Comment> comments) {
        for (OnCommentsResultListener listener : onCommentsResultListeners)
            listener.onGetComments(photoId, comments);
    }

    public void downvotePhoto(int photoId) {
        final RequestType requestType = RequestType.VOTE;
        VoteTable voteTable = new VoteTable(DbConnection.getInstance(context));
        VotePhotoAsyncTask task = new DownvotePhotoAsyncTask(voteTable, installationId, EXTERNAL_URI, photoId, new VotePhotoAsyncTask.OnVotePhotoResultListener() {
            @Override
            public void onPhotoVoted(int photoId, int newVoteCount) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotoVoted(photoId, newVoteCount);
            }

            @Override
            public void onPhotoVoteFailed(int photoId) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotoVoteFailed(photoId);
            }

            @Override
            public void onError(int photoId, Exception e) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
            }

            @Override
            public void onPhotoAlreadyVoted(int photo_id, int votecount) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotoAlreadyVoted(photo_id, votecount);
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute();
    }

    public void deleteComment(Comment comment) {
        final RequestType requestType = RequestType.COMMENT;
        DeleteCommentAsyncTask task = new DeleteCommentAsyncTask(installationId, EXTERNAL_URI, comment.getId(), new DeleteCommentAsyncTask.OnDeleteCommentResultListener() {
            @Override
            public void onCommentDeleted(int commentId) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnCommentDeleted(commentId);
            }

            @Override
            public void onCommentDeleteFailed(int commentId) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnCommentDeleteFailed(commentId);
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute();
    }

    public void deletePhoto(final Photo photo){
        final RequestType requestType = RequestType.PHOTOS;
        final int photoId = photo.getId();
        DeletePhotoAsyncTask task = new DeletePhotoAsyncTask(installationId, EXTERNAL_URI, photoId, new DeletePhotoAsyncTask.OnDeletePhotoResultListener() {
            @Override
            public void onPhotoDeleted(int photoId) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnPhotoDeleted(photoId);
            }

            @Override
            public void onPhotoDeleteFailed(int photoId) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnDeletePhotoFailed(photoId);
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute();
    }

    private void notifyOnDeletePhotoFailed(int photoId) {
        for (OnPhotoListener listener : onPhotosResultListeners)
            listener.onPhotoDeleteFailed(photoId);
    }

    private void notifyOnCommentDeleted(int commentId) {
        for (OnCommentsResultListener listener : onCommentsResultListeners)
            listener.onCommentDeleted(commentId);
    }

    private void notifyOnCommentDeleteFailed(int commentId) {
        for (OnCommentsResultListener listener : onCommentsResultListeners)
            listener.onCommentDeleteFailed(commentId);
    }

    public void sendComment(int photoId, String comment) {
        final RequestType requestType = RequestType.COMMENT;
        StoreCommentAsyncTask task = new StoreCommentAsyncTask(installationId, new StoreCommentAsyncTask.OnCommentSentListener() {
            @Override
            public void onCommentSent(Comment comment) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnNewComment(comment);
            }

            @Override
            public void onSendCommentFailed() {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnCommentSentFailed();
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute(new StoreCommentQuery(photoId, comment));
    }

    private void notifyOnCommentSentFailed() {
        for (OnCommentsResultListener listener : onCommentsResultListeners)
            listener.onSendCommentFailed();
    }

    private void notifyOnNewComment(Comment comment) {
        for (OnCommentsResultListener listener : onCommentsResultListeners)
            listener.onNewComment(comment);
    }

    public void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        if (!onPhotoUploadListeners.contains(onPhotoUploadListener))
            onPhotoUploadListeners.add(onPhotoUploadListener);
    }

    public void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener) {
        if (onPhotoUploadListeners.contains(onPhotoUploadListener))
            onPhotoUploadListeners.remove(onPhotoUploadListener);
    }

    public boolean hasOpenRequestsOfType(RequestType requestType) {
        if (openRequests.containsKey(requestType))
            return openRequests.get(requestType) > 0;
        return false;
    }

    public void searchPhotos(String query){
        searchPhotos(query, 1);
    }

    public void searchPhotos(final String query, int page) {
        final RequestType requestType = RequestType.SEARCH;
        SearchPhotosAsyncTask searchPhotosAsyncTask = new SearchPhotosAsyncTask(context, installationId, EXTERNAL_URI, query, page, new SearchPhotosAsyncTask.OnSearchPhotosResultCallback() {
            @Override
            public void onSearchPhotosResult(PhotoQueryResult photoQueryResult) {
                lastQuery = query;
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnSearchPhotosResult(photoQueryResult);
            }

            @Override
            public void onSearchPhotosError() {
                lastQuery = query;
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                notifyOnSearchPhotosError();
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        searchPhotosAsyncTask.execute();
    }

    private void notifyOnSearchPhotosError() {
        for (OnSearchPhotosResultListener listener : onSearchPhotosListeners)
            listener.onReceiveSearchedPhotosFailed();
    }

    private void notifyOnSearchPhotosResult(PhotoQueryResult photoQueryResult) {
        for (OnSearchPhotosResultListener listener : onSearchPhotosListeners)
            listener.onSearchedPhotosReceived(photoQueryResult);
    }

    public void searchNextPage(int page) {
        searchPhotos(lastQuery, page);
    }

    private static class WebSocketClient {

        private final String installationId;
        private final String url;
        private final AndroidSocket.OnMessageListener messageListener;
        private AndroidSocket androidSocket;

        public WebSocketClient(String url, String installationId, AndroidSocket.OnMessageListener messageListener){
            this.url = url;
            this.installationId = installationId;
            this.messageListener = messageListener;
        }

        public boolean connect() {
            IO.Options options = new IO.Options();
            try {
                //options.sslContext = AndroidSocket.createSslContext();
                options.reconnectionDelay = 5000;
                options.reconnection = true;
                //options.secure = true;
                options.transports = new String[]{WebSocket.NAME};
                options.reconnectionAttempts = 12;

                URI uri = URI.create(url + "/?token=" + installationId);
                if (androidSocket == null)
                    androidSocket = new AndroidSocket(options, uri, messageListener);
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

        public void disconnect() {
            androidSocket.disconnect();
        }
    }

    private void notifyOnPhotoDeleted(int photoId) {
        for (OnPhotoListener listener : onPhotosResultListeners)
            listener.onPhotoDeleted(photoId);
        for (OnPhotoListener listener : onPopularPhotosResultListeners)
            listener.onPhotoDeleted(photoId);
    }

    public boolean uploadPhoto(byte[] imageBytes, String comment) throws IOException, JSONException {
        final RequestType requestType = RequestType.UPLOAD;
        final JSONObject jsonObject = createJsonObject(imageBytes, comment);
        StorePhotoAsyncTask task = new StorePhotoAsyncTask(installationId, new StorePhotoAsyncTask.OnPhotoStoredCallback() {
            @Override
            public void onPhotoStoreSuccess(Photo photo) {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                Logger.log(TAG, LogLevel.INFO, "onPhotoStoreSuccess()");
                notifyPhotoUploadSucceded();
                onNewPhoto(photo);
            }

            @Override
            public void onPhotoStoreError() {
                removeOpenRequest(requestType);
                determineShouldDismissProgressDialog(requestType);
                Logger.log(TAG, LogLevel.INFO, "onPhotoStoreError()");
                notifyPhotoUploadFailed();
            }
        });
        addOpenRequest(requestType);
        determineShouldShowProgressDialog(requestType);
        task.execute(jsonObject);
        return true;
    }

    private void notifyPhotoUploadFailed() {
        for (OnPhotoUploadListener onPhotoUploadListener : onPhotoUploadListeners)
            onPhotoUploadListener.onPhotoUploadFailed();
    }

    private void notifyPhotoUploadSucceded() {
        for (OnPhotoUploadListener onPhotoUploadListener : onPhotoUploadListeners)
            onPhotoUploadListener.onPhotoUploaded();
    }

    private JSONObject createJsonObject(byte[] imageBytes, String comment) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("image", Base64.encodeToString(imageBytes, 0, imageBytes.length, Base64.DEFAULT));
        jsonObject.put("comment", comment);
        return jsonObject;
    }

    @Override
    public void onNewPhoto(Photo photo) {
        try {
            photo.saveToImageToCache(context);
            notifyOnNewPhoto(photo);
        } catch (IOException e) {
            Logger.log(TAG, LogLevel.ERROR, e.toString());
        }
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onConnectError(URI uri) {

    }

    @Override
    public void onNewComment(Comment comment) {
        notifyOnNewComment(comment);
    }

    @Override
    public void onCommentDeleted(int commentId) {
        notifyOnCommentDeleted(commentId);
    }

    @Override
    public void onPhotoDeleted(int photoId) {
        notifyOnPhotoDeleted(photoId);
    }

    @Override
    public void onNewVote(int photoId, int voteCount) {
        notifyOnPhotoVoted(photoId, voteCount);
    }

    public interface ServiceApiConnectionListener {
        void onConnected();
        void onDisconnected();
        void onConnectError();
    }

    public interface OnRequestListener {
        void onShowProgressDialog();
        void onDismissProgressDialog();
    }

    public interface OnPhotoListener extends OnRequestListener {
        void onNewPhoto(Photo photo);
        void onPhotoDeleted(int photoId);
        void onPhotoDeleteFailed(int photoId);
    }

    public interface OnPhotosResultListener extends OnPhotoListener {
        void onPhotosReceived(PhotoQueryResult result);
        void onReceivePhotosFailed();
    }

    public interface OnSearchPhotosResultListener extends OnPhotoListener {
        void onSearchedPhotosReceived(PhotoQueryResult result);
        void onReceiveSearchedPhotosFailed();
    }

    public interface OnPopularPhotosResultListener extends OnPhotoListener {
        void onPopularPhotosReceived(PhotoQueryResult result);
        void onReceivePopularPhotosFailed();
    }

    public interface OnPhotoVotedResultListener extends OnRequestListener{
        void onPhotoVoted(int photoId, int newVoteCount);
        void onPhotoAlreadyVoted(int photoId, int voteCount);
        void onPhotoVoteFailed(int photoId);
    }

    public interface OnCommentsResultListener extends OnRequestListener{
        void onGetComments(int photoId, List<Comment> comments);
        void onCommentDeleted(int commentId);
        void onCommentDeleteFailed(int commentId);
        void onNewComment(Comment comment);
        void onSendCommentFailed();
    }

    public interface OnPhotoUploadListener extends OnRequestListener{
        void onPhotoUploaded();
        void onPhotoUploadFailed();
    }
}
