package hochschuledarmstadt.photostream_tools.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PhotoQueryResult {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = new ArrayList<>();

    /**
     *
     * @return
     * The page
     */
    public Integer getPage() {
        return page;
    }

    /**
     *
     * @return
     * The items
     */
    public List<Photo> getPhotos() {
        return photos;
    }


}