package hochschuledarmstadt.photostream_tools;

import org.json.JSONException;

import java.io.IOException;

import hochschuledarmstadt.photostream_tools.callback.OnCommentsResultListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoVotedResultListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosResultListener;
import hochschuledarmstadt.photostream_tools.callback.OnPopularPhotosResultListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchPhotosResultListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.Photo;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface IPhotoStreamClient {
    void addOnPhotoVotedResultListener(OnPhotoVotedResultListener onPhotoVotedResultListener);
    void removeOnPhotoVotedResultListener(OnPhotoVotedResultListener onPhotoVotedResultListener);
    void addOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener);
    void removeOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener);
    void addOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener);
    void removeOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener);
    void addOnPopularPhotosResultListener(OnPopularPhotosResultListener onPopularPhotosResultListener);
    void removeOnPopularPhotosResultListener(OnPopularPhotosResultListener onPopularPhotosResultListener);
    void addOnSearchPhotosResultListener(OnSearchPhotosResultListener listener);
    void removeOnSearchPhotosResultListener(OnSearchPhotosResultListener listener);
    void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);
    void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);
    void getPhotos(int page);
    void getPopularPhotos(int page);
    void upvotePhoto(int photoId);
    boolean hasUserAlreadyVotedForPhoto(int photoId);
    void getComments(int photoId);
    void downvotePhoto(int photoId);
    void deleteComment(Comment comment);
    void deletePhoto(Photo photo);
    void sendComment(int photoId, String comment);
    boolean hasOpenRequestsOfType(RequestType requestType);
    void searchPhotos(String query);
    void searchPhotos(String query, int page);
    void searchNextPage(int page);
    boolean uploadPhoto(byte[] imageBytes, String comment) throws IOException, JSONException;
}
