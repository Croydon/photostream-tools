package hochschuledarmstadt.photostream_tools.callback;

import java.util.List;

import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnCommentsResultListener extends OnRequestListener {
    void onGetComments(int photoId, List<Comment> comments);
    void onGetCommentsFailed(int photoId, HttpResult httpResult);
    void onCommentDeleted(int commentId);
    void onDeleteCommentFailed(int commentId, HttpResult httpResult);
    void onNewComment(Comment comment);
    void onSendCommentFailed(HttpResult httpResult);
}
