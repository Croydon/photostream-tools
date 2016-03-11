package hochschuledarmstadt.photostream_tools.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment implements Parcelable, Id {

    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("comment_id")
    @Expose
    private Integer commentId;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("deleteable")
    @Expose
    private boolean deleteable;
    /**
     *
     * @return
     * The commentId
     */
    public Integer getId() {
        return commentId;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    public boolean isDeleteable() {
        return deleteable;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    protected Comment(Parcel in) {
        photoId = in.readInt();
        commentId = in.readInt();
        message = in.readString();
        deleteable = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(photoId);
        dest.writeInt(commentId);
        dest.writeString(message);
        dest.writeInt(deleteable ? 1 : 0);
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
