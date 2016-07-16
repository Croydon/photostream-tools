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

import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentUploadFailedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewCommentReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;

/**
 * Über dieses Interface kann mit dem PhotoStream Server kommuniziert werden.
 */
public interface IPhotoStreamClient {

    /**
     * Registriert einen Listener des Typs {@link OnPhotoLikeListener}. <br>
     * Die Ergebnisse von den Methodenaufrufen {@link IPhotoStreamClient#likePhoto(int)} und {@link IPhotoStreamClient#resetLikeForPhoto(int)} <br>
     * werden über die Methoden des Interfaces {@link OnPhotoLikeListener} zurück geliefert
     * @param onPhotoLikeListener listener
     */
    void addOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);

    /**
     * Entfernt den Listener {@code onPhotoLikeListener}
     * @param onPhotoLikeListener listener
     */
    void removeOnPhotoLikeListener(OnPhotoLikeListener onPhotoLikeListener);

    /**
     * Registriert einen Listener des Typs {@link OnCommentsReceivedListener}. <br>
     * Das Ergebnis von dem Methodenaufruf {@link IPhotoStreamClient#loadComments(int)} <br>
     * wird über die Methoden des Interfaces {@link OnCommentsReceivedListener} zurück geliefert
     * @param onCommentsReceivedListener listener
     */
    void addOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener);

    /**
     * Entfernt den Listener {@code onCommentsReceivedListener}
     * @param onCommentsReceivedListener listener
     */
    void removeOnCommentsReceivedListener(OnCommentsReceivedListener onCommentsReceivedListener);

    /**
     * Registriert einen Listener des Typs {@link OnPhotosReceivedListener}. <br>
     * Die Ergebnisse von den Methodenaufrufen {@link IPhotoStreamClient#loadPhotos()} und {@link IPhotoStreamClient#loadMorePhotos()} <br>
     * werden über die Methoden des Interfaces {@link OnPhotosReceivedListener} zurück geliefert
     * @param onPhotosReceivedListener listener
     */
    void addOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener);

    /**
     * Entfernt den Listener {@code onPhotosReceivedListener}
     * @param onPhotosReceivedListener listener
     */
    void removeOnPhotosReceivedListener(OnPhotosReceivedListener onPhotosReceivedListener);

    /**
     * Registriert einen Listener des Typs {@link OnSearchedPhotosReceivedListener}. <br>
     * Die Ergebnisse von den Methodenaufrufen {@link IPhotoStreamClient#searchPhotos(String)} und {@link IPhotoStreamClient#searchMorePhotos()} <br>
     * werden über die Methoden des Interfaces {@link OnSearchedPhotosReceivedListener} zurück geliefert
     * @param onSearchedPhotosReceivedListener listener
     */
    void addOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener);

    /**
     * Entfernt den Listener {@code onSearchedPhotosReceivedListener}
     * @param onSearchedPhotosReceivedListener listener
     */
    void removeOnSearchPhotosResultListener(OnSearchedPhotosReceivedListener onSearchedPhotosReceivedListener);

    /**
     * Registriert einen Listener des Typs {@link OnPhotoUploadListener}. <br>
     * Das Ergebnis von dem Methodenaufruf {@link IPhotoStreamClient#uploadPhoto(byte[], String)}} <br>
     * wird über die Methoden des Interfaces {@link OnPhotoUploadListener} zurück geliefert
     * @param onPhotoUploadListener listener
     */
    void addOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);

    /**
     * Entfernt den Listener {@code onPhotoUploadListener}
     * @param onPhotoUploadListener listener
     */
    void removeOnPhotoUploadListener(OnPhotoUploadListener onPhotoUploadListener);

    /**
     * Registriert einen Listener des Typs {@link OnPhotoDeletedListener}. <br>
     * Das Ergebnis von dem Methodenaufruf {@link IPhotoStreamClient#deletePhoto(int)} <br>
     * wird über die Methoden des Interfaces {@link OnPhotoDeletedListener} zurück geliefert
     * @param onPhotoDeletedListener listener
     */
    void addOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener);

    /**
     * Entfernt den Listener {@code onPhotoDeletedListener}
     * @param onPhotoDeletedListener listener
     */
    void removeOnPhotoDeletedListener(OnPhotoDeletedListener onPhotoDeletedListener);

    /**
     * Registriert einen Listener des Typs {@link OnNewPhotoReceivedListener}. <br>
     * Wenn ein neues Photo über den Server veröffentlicht wurde, dann wird das neue Photo
     * über die Methoden des Interfaces {@link OnNewPhotoReceivedListener} zurück geliefert
     * @param onNewPhotoReceivedListener listener
     */
    void addOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener);

    /**
     * Entfernt den Listener {@code onNewPhotoReceivedListener}
     * @param onNewPhotoReceivedListener listener
     */
    void removeOnNewPhotoReceivedListener(OnNewPhotoReceivedListener onNewPhotoReceivedListener);

    /**
     * Registriert einen Listener des Typs {@link OnNewCommentReceivedListener}. <br>
     * Wenn ein neuer Kommentar über den Server veröffentlicht wurde, dann wird der neue Kommentar <br>
     * über die Methoden des Interfaces {@link OnNewCommentReceivedListener} zurück geliefert
     * @param onNewCommentReceivedListener listener
     */
    void addOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener);

    /**
     * Entfernt den Listener {@code onNewCommentReceivedListener}
     * @param onNewCommentReceivedListener listener
     */
    void removeOnNewCommentReceivedListener(OnNewCommentReceivedListener onNewCommentReceivedListener);

    /**
     * Registriert einen Listener des Typs {@link OnCommentUploadFailedListener}. <br>
     * Schlägt das Veröffentlichen des Kommentars über den Methodenaufruf {@link IPhotoStreamClient#uploadComment(int, String)} fehl, <br>
     * dann wird über die Methoden des Interfaces {@link OnCommentUploadFailedListener} der Fehler zurück geliefert. <br>
     * Wurde der Kommentar veröffentlicht, dann liefert das Interface {@link OnNewCommentReceivedListener} den Kommentar zurück.
     * @param onCommentUploadFailedListener listener
     */
    void addOnUploadCommentFailedListener(OnCommentUploadFailedListener onCommentUploadFailedListener);

    /**
     * Entfernt den Listener {@code onCommentUploadListener}
     * @param onCommentUploadFailedListener listener
     */
    void removeOnUploadCommentFailedListener(OnCommentUploadFailedListener onCommentUploadFailedListener);

    /**
     * Registriert einen Listener des Typs {@link OnCommentDeletedListener}. <br>
     * Die Ergebnisse von dem Methodenaufruf {@link IPhotoStreamClient#deleteComment(int)} <br>
     * werden über die Methoden des Interfaces {@link OnCommentDeletedListener} zurück geliefert
     * @param onCommentDeletedListener listener
     */
    void addOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener);

    /**
     * Entfernt den Listener {@code onCommentDeletedListener}
     * @param onCommentDeletedListener listener
     */
    void removeOnCommentDeletedListener(OnCommentDeletedListener onCommentDeletedListener);

    /**
     * Registriert einen Listeners des Typs {@link OnCommentCountChangedListener}. <br>
     * Wenn sich die Anzahl der Kommentare zu einem Photo ändert, dann wird die neue Anzahl der Kommentare an die <br>
     * Methode {@link OnCommentCountChangedListener#onCommentCountChanged(int, int) übergeben.
     * @param onCommentCountChangedListener listener
     */
    void addOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener);

    /**
     * Entfernt den Listener {@code onCommentCountChangedListener}
     * @param onCommentCountChangedListener listener
     */
    void removeOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener);

    /**
     * Asynchroner Aufruf. <br>
     * Veröffentlicht ein Photo {@code imageBytes} über den Server.
     * Verwenden Sie die Methode BitmapUtils.bitmapToBytes(Bitmap bitmap) um ein Bitmap in bytes zu konvertieren
     * Das Ergebnis dieses Aufrufs (<i>erfolgreich</i> oder <i>nicht erfolgreich</i> wird über den Listener {@link OnPhotoUploadListener} zurück geliefert.
     * Das neue Photo wird über den Listener {@link OnNewPhotoReceivedListener} zurück geliefert
     * @param imageBytes das Photo als Byte Array
     * @param description die Beschreibung zu dem Photo
     * @throws IOException
     * @throws JSONException
     */
    void uploadPhoto(byte[] imageBytes, String description) throws IOException, JSONException;

    /**
     * Asynchroner Aufruf. <br>
     * Lädt die erste Seite von Photos aus dem Stream. <br>
     * Das Ergebnis wird über den Listener {@link OnPhotosReceivedListener} zurück geliefert
     */
    void loadPhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Lädt die nächste Seite von Photos aus dem Stream. <br>
     * Das Ergebnis wird über den Listener {@link OnPhotosReceivedListener} zurück geliefert
     */
    void loadMorePhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Serverseitige Suche von Photos anhand der Photobeschreibung {@code query}. <br>
     * Das Ergebnis ist die erste Seite der Suche. <br>
     * Das Ergebnis wird über den Listener {@link OnSearchedPhotosReceivedListener} zurück geliefert.
     * @param query Beschreibung zu dem Photo
     */
    void searchPhotos(String query);

    /**
     * Asynchroner Aufruf. <br>
     * Lädt die nächste Seite aus dem vorherigen Suchergebnis. <br>
     * Das Ergebnis wird über den Listener {@link OnSearchedPhotosReceivedListener} zurück geliefert.
     */
    void searchMorePhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Löscht ein Photo von dem Server <br>
     * Das Ergebnis wird über den Listener {@link OnPhotoDeletedListener} zurück geliefert.
     * @param photoId id des zu löschenden Photos
     */
    void deletePhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Liked ein Photo mit der id {@code photoId} <br>
     * Das Ergebnis wird über den Listener {@link OnPhotoLikeListener} zurück geliefert.
     * @param photoId id des Photos, welches geliked werden soll
     */
    void likePhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Entfernt einen Like von einem Photo mit der id {@code photoId} <br>
     * Das Ergebnis wird über den Listener {@link OnPhotoLikeListener} zurück geliefert.
     * @param photoId id des Photos von dem der Like entfernt werden soll
     */
    void resetLikeForPhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Fragt alle Kommentare zu dem Photo mit der id {@code photoId} von dem Server ab. <br>
     * Das Ergebnis wird über den Listener {@link OnCommentsReceivedListener} zurück geliefert.
     * @param photoId id des Photos
     */
    void loadComments(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Sendet einen Kommentar an den Server. Der Kommentar {@code comment} wird zum Photo mit der id {@code photoId} gespeichert. <br>
     * Das Ergebnis wird über den Listener {@link OnCommentUploadFailedListener} zurück geliefert.
     * @param photoId id des Photos
     * @param comment Kommentar zu dem Photo
     */
    void uploadComment(int photoId, String comment);

    /**
     * Asynchroner Aufruf. <br>
     * Löscht einen Kommentar von dem Server mit der id {@code commentId} <br>
     * Das Ergebnis wird über den Listener {@link OnCommentDeletedListener} zurück geliefert.
     * @param commentId der Kommentar, der gelöscht werden soll
     */
    void deleteComment(int commentId);

    /**
     * Synchroner Aufruf. <br>
     * Mit dieser Methode kann abgefragt werden, ob aktuell ein Request zu einer bestimmten Kategorie {@link RequestType} momentan verarbeitet
     * @param requestType Typ des Requests ({@link RequestType})
     * @return {@code true}, wenn mindestens ein Request aus der angegebenen Kategorie verarbeitet wird, ansonsten {@code false}
     */
    boolean hasOpenRequestsOfType(RequestType requestType);
}
