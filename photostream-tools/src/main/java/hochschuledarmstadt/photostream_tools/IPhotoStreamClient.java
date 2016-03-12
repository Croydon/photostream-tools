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

public interface IPhotoStreamClient {
    void addOnPhotoVotedResultListener(OnPhotoVotedResultListener onPhotoVotedResultListener);
    void removeOnPhotoVotedResultListener(OnPhotoVotedResultListener onPhotoVotedResultListener);
    void addOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener);
    void removeOnGetCommentsResultListener(OnCommentsResultListener onCommentsResultListener);
    void addOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener);
    void removeOnPhotosResultListener(OnPhotosResultListener onPhotosResultListener);
    void addOnPopularPhotosResultListener(OnPopularPhotosResultListener onPopularPhotosResultListener);
    void removeOnPopularPhotosResultListener(OnPopularPhotosResultListener onPopularPhotosResultListener);
    void addOnSearchPhotosResultListener(OnSearchPhotosResultListener onSearchPhotosResultListener);
    void removeOnSearchPhotosResultListener(OnSearchPhotosResultListener onSearchPhotosResultListener);
    void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);
    void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);
    boolean uploadPhoto(byte[] imageBytes, String comment) throws IOException, JSONException;
    void getPhotos(int page);
    void getPhotos();
    void getPopularPhotos(int page);
    void searchPhotos(String query);
    void searchPhotos(String query, int page);
    void searchPhotosNextPage(int page);
    void deletePhoto(Photo photo);
    void upvotePhoto(int photoId);
    void downvotePhoto(int photoId);
    boolean hasUserAlreadyVotedForPhoto(int photoId);
    void getComments(int photoId);
    void sendComment(int photoId, String comment);
    void deleteComment(Comment comment);
    boolean hasOpenRequestsOfType(RequestType requestType);
}
