package hochschuledarmstadt.photostream_tools.model;

/**
 * Created by Andreas Schattney on 24.02.2016.
 */
public class StoreCommentQuery {

    private final String comment;
    private final int photoId;

    public StoreCommentQuery(int photoId, String comment){
        this.photoId = photoId;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public int getPhotoId() {
        return photoId;
    }
}
