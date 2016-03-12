package hochschuledarmstadt.photostream_tools.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommentQueryResult {

    @SerializedName("photo_id")
    @Expose
    private int photoId;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = new ArrayList<>();

    /**
     *
     * @return
     * The photoId
     */
    public int getPhotoId() {
        return photoId;
    }

    /**
     *
     * @return
     * The comments
     */
    public List<Comment> getComments() {
        return comments;
    }


}