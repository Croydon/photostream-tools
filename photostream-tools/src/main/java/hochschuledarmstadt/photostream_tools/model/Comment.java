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

/**
 * Diese Klasse repräsentiert einen anonymen Kommentar zu einem Photo
 */
public class Comment extends BaseItem implements Parcelable {

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
     * Liefert die id des Kommentars zurück
     * @return id
     */
    public int getId() {
        return commentId;
    }

    /**
     * Liefert den Inhalt des Kommentars zurück
     * @return inhalt
     */
    public String getMessage() {
        return message;
    }

    /**
     * Über diese Funktion kann bestimmt werden, ob das aktuelle Gerät berechtigt ist diesen Kommentar zu löschen.
     * @return {@code true}, wenn der Kommentar von dem aktuellen Gerät veröffentlicht wurde, ansonsten {@code false}
     */
    public boolean isDeleteable() {
        return deleteable;
    }

    /**
     * Liefert die id des Photos zurück
     * @return photo id
     */
    public int getPhotoId() {
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
