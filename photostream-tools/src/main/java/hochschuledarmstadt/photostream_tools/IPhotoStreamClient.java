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

package hochschuledarmstadt.photostream_tools;

import org.json.JSONException;

import java.io.IOException;

import hochschuledarmstadt.photostream_tools.callback.OnCommentsListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchPhotosResultListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.Photo;

public interface IPhotoStreamClient {

    /**
     * Registriert einen Listener der aufgerufen wird, wenn ein Photo geliked oder disliked wird
     * @param onPhotoLikeListener
     */
    void addOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);
    void removeOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);

    /**
     * Registriert einen Listener der aufgerufen wird, wenn Kommentare zu einem Photo geladen wurden
     * @param onCommentsListener listener
     */
    void addOnCommentsListener(OnCommentsListener onCommentsListener);
    void removeOnCommentsListener(OnCommentsListener onCommentsListener);

    /**
     * Registriert einen Listener der aufgerufen wird, wenn Photos aus dem Stream geladen wurden
     * @param onPhotosListener listener
     */
    void addOnPhotosListener(OnPhotosListener onPhotosListener);
    void removeOnPhotosListener(OnPhotosListener onPhotosListener);

    /**
     * Registriert einen Listener der aufgerufen wird, wenn gesuchte Photos geladen wurden
     * @param onSearchPhotosResultListener
     */
    void addOnSearchPhotosResultListener(OnSearchPhotosResultListener onSearchPhotosResultListener);
    void removeOnSearchPhotosResultListener(OnSearchPhotosResultListener onSearchPhotosResultListener);
    /**
     * Registriert einen Listener der aufgerufen wird, wenn ein Photo an den Server gesendet wurde
     * @param onPhotoUploadListener listener
     */
    void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);
    void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);

    /**
     * Sendet ein Photo {@code imageBytes} an den PhotoStream Server.
     * Verwenden Sie die Methode BitmapUtils.bitmapToBytes(Bitmap bitmap) um ein Bitmap in bytes zu konvertieren
     * @param imageBytes image as bytes
     * @param comment the comment for the photo
     * @return true
     * @throws IOException
     * @throws JSONException
     */
    boolean uploadPhoto(byte[] imageBytes, String comment) throws IOException, JSONException;

    /**
     * Lädt die erste Seite von Photos aus dem Stream
     * @param photosDisplayedInStream {@code true} übergeben, wenn Photos im lokalen Stream vorhanden sind, ansonsten {@code false}
     */
    void loadPhotos(boolean photosDisplayedInStream);

    /**
     * Lädt die nächste Seite von Photos aus dem Stream.
     * Asynchroner Aufruf.
     * Das Ergebnis wird über einen Listener zurück geliefert.
     */
    void loadMorePhotos();

    /**
     * Serverseitige Suche von Photos anhand der Photobeschreibung.
     * Asynchroner Aufruf.
     * Das Ergebnis ist die erste Seite der Suche.
     * Das Ergebnis wird über einen Listener zurück geliefert.
     * @param query Photobeschreibung
     */
    void searchPhotos(String query);

    /**
     * Lädt die nächste Seite aus dem Suchergebnis.
     * Asynchroner Aufruf.
     * Das Ergebnis wird über einen Listener zurück geliefert.
     */
    void searchMorePhotos();

    /**
     * Löscht ein Photo von dem PhotoStream Server
     * Asynchroner Aufruf.
     * Das Ergebnis wird über einen Listener zurück geliefert.
     * @param photo das zu löschende Photo
     */
    void deletePhoto(Photo photo);

    /**
     * Liked ein Photo aus dem PhotoStream
     * @param photoId id des Photos, welches geliked werden soll
     */
    void likePhoto(int photoId);

    /**
     * Entfernt einen Like von einem Photo
     * @param photoId id des Photos von dem der Like entfernt werden soll
     */
    void resetLikeForPhoto(int photoId);

    /**
     * Diese Methode liefert zurück, ob der Nutzer das Photo mit der id {@code photoId} geliked hat
     * @param photoId id des Photos
     * @return {@code true}, wenn der Nutzer das Photo aktuell geliked hat, ansonsten {@code false}
     */
    boolean hasUserLikedPhoto(int photoId);

    /**
     * Fragt alle Kommentare zu dem Photo mit der id {@code photoId} von dem Server ab.
     * @param photoId id des Photos
     */
    void loadComments(int photoId);

    /**
     * Sendet einen Kommentar an den Server. Der Kommentar {@code comment} wird zum Photo mit der id {@code photoId} gespeichert
     * @param photoId id des Photos
     * @param comment Kommentar zu dem Photo
     */
    void uploadComment(int photoId, String comment);

    /**
     * Löscht einen Kommentar von dem Server
     * @param comment der Kommentar, der gelöscht werden soll
     */
    void deleteComment(Comment comment);

    /**
     * Mit dieser Methode kann abgefragt werden, ob aktuell ein Request zu einer bestimmten Kategorie {@code requestType} momentan verarbeitet
     * @param requestType der Requesttyp
     * @return {@code true}, wenn mindestens ein Request aus der angegebenen Kategorie verarbeitet wird, ansonsten {@code false}
     */
    boolean hasOpenRequestsOfType(RequestType requestType);
}
