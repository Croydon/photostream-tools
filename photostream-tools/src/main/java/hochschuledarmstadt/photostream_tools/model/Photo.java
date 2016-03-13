package hochschuledarmstadt.photostream_tools.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Photo implements Parcelable, Id{

    private static final String TAG = Photo.class.getName();

    @SerializedName("image")
    @Expose
    private String imageFilePath;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("votecount")
    @Expose
    private Integer votecount;
    @SerializedName("photo_id")
    @Expose
    private int id;
    @SerializedName("deleteable")
    @Expose
    private boolean deleteable;

    /**
     *
     * @return
     * The imageFilePath
     */
    public String getImageFilePath() {
        return imageFilePath;
    }

    /**
     *
     * @return
     * The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     *
     * @return
     * The votecount
     */
    public Integer getVotecount() {
        return votecount;
    }

    public void updateVotecount(Integer votecount) {
        this.votecount = votecount;
    }

    public boolean isDeleteable() {
        return deleteable;
    }

    protected Photo(Parcel in) {
        id = in.readInt();
        imageFilePath = in.readString();
        comment = in.readString();
        votecount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imageFilePath);
        dest.writeString(comment);
        dest.writeInt(votecount);
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void saveToImageToCache(Context context) throws IOException {

        String filename = String.format("%s.jpg", id);
        if (!imageExistsOnFileSystem(context, filename)) {
            FileOutputStream outputStream = null;
            try {
                outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                byte[] data = Base64.decode(imageFilePath, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                bitmap.recycle();
                imageFilePath = new File(context.getFilesDir(), filename).getAbsolutePath();
            } catch (Exception e) {

            } finally {
                if (outputStream != null)
                    outputStream.close();
            }
        }else{
            imageFilePath = filename;
        }

    }

    private boolean imageExistsOnFileSystem(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return false;
    }
}