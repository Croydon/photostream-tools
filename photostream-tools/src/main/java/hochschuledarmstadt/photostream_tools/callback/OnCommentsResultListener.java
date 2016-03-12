package hochschuledarmstadt.photostream_tools.callback;

import java.util.List;

import hochschuledarmstadt.photostream_tools.model.Comment;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnCommentsResultListener extends OnRequestListener {
    void onGetComments(int photoId, List<Comment> comments);

    void onCommentDeleted(int commentId);

    void onCommentDeleteFailed(int commentId);

    void onNewComment(Comment comment);

    void onSendCommentFailed();
}
