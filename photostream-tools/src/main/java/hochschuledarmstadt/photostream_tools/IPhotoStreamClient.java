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

import hochschuledarmstadt.photostream_tools.callback.OnCommentDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewCommentReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchPhotosResultListener;
import hochschuledarmstadt.photostream_tools.callback.OnUploadCommentListener;

public interface IPhotoStreamClient {

    /**
     * Registriert einen Listener der aufgerufen wird, wenn ein Photo geliked wird oder ein Like von einem Photo entfernt wird
     * @param onPhotoLikeListener
     */
    void addOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);
    void removeOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);

    /**
     * Registriert einen Listener der aufgerufen wird, wenn Kommentare zu einem Photo geladen wurden
     * @param onCommentsReceivedListener listener
     */
    void addOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener);
    void removeOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener);

    /**
     * Registriert einen Listener der aufgerufen wird, wenn Photos vom Server geladen wurden
     * @param onPhotosReceivedListener listener
     */
    void addOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener);
    void removeOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener);

    /**
     * Registriert einen Listener der aufgerufen wird, wenn gesuchte Photos vom Server geladen wurden
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

    void addOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener);
    void removeOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener);

    void addOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener);
    void removeOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener);

    void addOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener);
    void removeOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener);

    void addOnUploadCommentListener(OnUploadCommentListener onUploadCommentListener);
    void removeOnUploadCommentListener(OnUploadCommentListener onUploadCommentListener);

    void addOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener);
    void removeOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener);

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
     * Asynchroner Aufruf. <br>
     * Lädt die erste Seite von Photos aus dem Stream. <br>
     * Abgerufene Photos werden über den Listener {@code OnPhotosListener} zurückgeliefert
     */
    void loadPhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Lädt die nächste Seite von Photos aus dem Stream. <br>
     * Abgerufene Photos werden über den Listener {@code OnPhotosListener} zurückgeliefert
     */
    void loadMorePhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Serverseitige Suche von Photos anhand der Photobeschreibung {@code query}. <br>
     * Das Ergebnis ist die erste Seite der Suche. <br>
     * Photos werden über den Listener {@code OnSearchPhotosResultListener} zurückgeliefert
     * @param query Beschreibung zu dem Photo
     */
    void searchPhotos(String query);

    /**
     * Asynchroner Aufruf. <br>
     * Lädt die nächste Seite aus dem vorherigen Suchergebnis. <br>
     * Photos werden über den Listener {@code OnSearchPhotosResultListener} zurückgeliefert
     */
    void searchMorePhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Löscht ein Photo von dem Server <br>
     * Das Ergebnis wird an Listener des Typs {@code OnPhotosListener} und {@code OnSearchPhotosResultListener} zurückgeliefert. <br>
     * @param photoId id des zu löschenden Photos
     */
    void deletePhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Liked ein Photo mit der id {@code photoId} <br>
     * Das Ergebnis wird an Listener des Typs {@code OnPhotoLikeListener} zurückgeliefert. <br>
     * @param photoId id des Photos, welches geliked werden soll
     */
    void likePhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Entfernt einen Like von einem Photo mit der id {@code photoId} <br>
     * Das Ergebnis wird an Listener des Typs {@code OnPhotoLikeListener} zurückgeliefert. <br>
     * @param photoId id des Photos von dem der Like entfernt werden soll
     */
    void resetLikeForPhoto(int photoId);

    /**
     * Synchroner Aufruf. <br>
     * Diese Methode liefert zurück, ob der Nutzer das Photo mit der id {@code photoId} aktuell geliked hat <br>
     * @param photoId id des Photos
     * @return {@code true}, wenn der Nutzer das Photo aktuell geliked hat, ansonsten {@code false}
     */
    boolean hasUserLikedPhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Fragt alle Kommentare zu dem Photo mit der id {@code photoId} von dem Server ab. <br>
     * Das Ergebnis wird an Listener des Typs {@code OnCommentsListener} zurückgeliefert. <br>
     * @param photoId id des Photos
     */
    void loadComments(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Sendet einen Kommentar an den Server. Der Kommentar {@code comment} wird zum Photo mit der id {@code photoId} gespeichert. <br>
     * Das Ergebnis wird an Listener des Typs {@code OnCommentsListener} zurückgeliefert. <br>
     * @param photoId id des Photos
     * @param comment Kommentar zu dem Photo
     */
    void uploadComment(int photoId, String comment);

    /**
     * Asynchroner Aufruf. <br>
     * Löscht einen Kommentar von dem Server mit der id {@code commentId} <br>
     * Das Ergebnis wird an Listener des Typs {@code OnCommentsListener} zurückgeliefert. <br>
     * @param commentId der Kommentar, der gelöscht werden soll
     */
    void deleteComment(int commentId);

    /**
     * Synchroner Aufruf. <br>
     * Mit dieser Methode kann abgefragt werden, ob aktuell ein Request zu einer bestimmten Kategorie {@code requestType} momentan verarbeitet
     * @param requestType der Requesttyp
     * @return {@code true}, wenn mindestens ein Request aus der angegebenen Kategorie verarbeitet wird, ansonsten {@code false}
     */
    boolean hasOpenRequestsOfType(RequestType requestType);
}
