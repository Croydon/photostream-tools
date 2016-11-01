/*
 * The MIT License
 *
 * Copyright (c) 2016 Andreas Schattney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hochschuledarmstadt.photostream_tools.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;

/**
 * Repräsentiert ein Photo aus dem Photo Stream
 */
public class Photo extends BaseItem implements Parcelable{

    private static final String TAG = Photo.class.getName();

    @SerializedName("image")
    @Expose
    private String imageFilePath;
    @SerializedName("comment")
    @Expose
    private String description;
    @SerializedName("favorite")
    @Expose
    private int favorite;
    @SerializedName("photo_id")
    @Expose
    private int id;
    @SerializedName("deleteable")
    @Expose
    private boolean deleteable;
    @SerializedName("comment_count")
    @Expose
    private int commentCount;

    /**
     * Liefert den absoluten Dateipfad zurück, an dem das Photo abgespeichert ist
     * @return {@link String} absoluten Dateipfad
     */
    public String getImageFilePath() {
        return imageFilePath;
    }

    /**
     * Liefert den absoluten Dateipfad des Photos als {@link File}
     * @return {@link File}
     */
    public File getImageFile() {
        return new File(imageFilePath);
    }

    /**
     * Liefert die Beschreibung zu dem Photo zurück
     * @return Beschreibung
     */
    public String getDescription() {
        return description;
    }

    /**
     * Liefert zurück ob das Photo über das aktuelle Gerät favorisiert ist.
     * @return {@code true}, wenn das Photo favorisiert ist, ansonsten {@code false}
     */
    public boolean isFavorite() {
        return favorite == 1;
    }

    /**
     * Über diese Funktion kann bestimmt werden, ob das aktuelle Gerät berechtigt ist das Photo zu löschen.
     * @return {@code true}, wenn das Photo von dem aktuellen Gerät erzeugt wurde, ansonsten {@code false}
     */
    public boolean isDeleteable() {
        return deleteable;
    }

    /**
     * Liefert die Anzahl der Kommentare zu einem Photo
     * @return {@link Integer} Anzahl der Kommentare
     */
    public int getCommentCount() {
        return commentCount;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite ? 1 : 0;
    }

    /**
     * Liefert die id des Photos zurück
     * @return {@code Integer} id des Photos
     */
    public int getId() {
        return id;
    }

    protected Photo(Parcel in) {
        id = in.readInt();
        imageFilePath = in.readString();
        description = in.readString();
        favorite = in.readInt();
        deleteable = in.readInt() == 1;
        commentCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(imageFilePath);
        dest.writeString(description);
        dest.writeInt(favorite);
        dest.writeInt(deleteable ? 1 : 0);
        dest.writeInt(commentCount);
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

}