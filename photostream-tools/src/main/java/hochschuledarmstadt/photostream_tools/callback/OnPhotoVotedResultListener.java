package hochschuledarmstadt.photostream_tools.callback;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public interface OnPhotoVotedResultListener extends OnRequestListener {
    void onPhotoVoted(int photoId, int newVoteCount);

    void onPhotoAlreadyVoted(int photoId, int voteCount);

    void onPhotoVoteFailed(int photoId);
}
