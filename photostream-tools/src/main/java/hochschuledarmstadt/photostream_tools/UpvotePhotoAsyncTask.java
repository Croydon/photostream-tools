package hochschuledarmstadt.photostream_tools;

/**
 * Created by Andreas Schattney on 08.03.2016.
 */
class UpvotePhotoAsyncTask extends VotePhotoAsyncTask {

    public UpvotePhotoAsyncTask(VoteTable voteTable, String installationId, String uri, int photoId, OnVotePhotoResultListener callback) {
        super(voteTable, installationId, uri, photoId, callback);
    }

    protected String buildUri(String uri, int photoId) {
        return String.format("%s/photostream/image/%s/upvote", uri, photoId);
    }

}
