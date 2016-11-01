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

import android.graphics.Bitmap;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import hochschuledarmstadt.photostream_tools.callback.OnCommentCountChangedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentUploadFailedListener;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewCommentReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnNewPhotoReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoDeletedListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoFavoredListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.callback.OnRequestListener;
import hochschuledarmstadt.photostream_tools.callback.OnSearchedPhotosReceivedListener;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;
import hochschuledarmstadt.photostream_tools.model.PhotoQueryResult;
import android.content.Intent;

/**
 * Über dieses Interface kann mit dem Server kommuniziert werden.
 */
public interface IPhotoStreamClient {

    /**
     * Action des Intents, der gesendet wird, wenn ein anderes Android Gerät ein neues Photo hochgeladen hat.
     */
    String INTENT_ACTION_NEW_PHOTO_AVAILABLE = "hochschuledarmstadt.photostream_tools.intent.NEW_PHOTO";

    /**
     * Intent mit der Action {@link IPhotoStreamClient#INTENT_ACTION_NEW_PHOTO_AVAILABLE} enthält zusätzlich das Photo Objekt. <br>
     * Dieses kann über die Methode {@link Intent#getParcelableExtra(String)} referenziert werden. <br> <br>
     * Photo photo = intent.getParcelableExtra(IPhotoStreamClient.INTENT_KEY_PHOTO); <br>
     */
    String INTENT_KEY_PHOTO = "INTENT_KEY_NEW_PHOTO";

    void addOnRequestListener(OnRequestListener onRequestListener, RequestType... requestTypes);

    void removeOnRequestListener(OnRequestListener onRequestListener);

    /**
     * Registriert einen Listener des Typs {@link OnPhotoFavoredListener}. <br>
     * Die Ergebnisse von den Methodenaufrufen {@link IPhotoStreamClient#favoritePhoto(int)} und {@link IPhotoStreamClient#unfavoritePhoto(int)} <br>
     * werden über die Methoden des Interfaces {@link OnPhotoFavoredListener} zurück geliefert
     * @param onPhotoFavoredListener listener
     */
    void addOnPhotoFavoriteListener(OnPhotoFavoredListener onPhotoFavoredListener);

    /**
     * Entfernt den Listener {@code onPhotoFavoredListener}
     * @param onPhotoFavoredListener listener
     */
    void removeOnPhotoFavoriteListener(OnPhotoFavoredListener onPhotoFavoredListener);

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
     * wird über die Methoden des Interfaces {@link OnPhotoUploadListener} zurück geliefert <br>
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
     * wird über die Methoden des Interfaces {@link OnPhotoDeletedListener} zurück geliefert <br>
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
     * über die Methoden des Interfaces {@link OnNewCommentReceivedListener} zurück geliefert <br>
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
     * werden über die Methoden des Interfaces {@link OnCommentDeletedListener} zurück geliefert <br>
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
     * Methode {@link OnCommentCountChangedListener#onCommentCountChanged(int, int)} übergeben. <br>
     * @param onCommentCountChangedListener listener
     */
    void addOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener);

    /**
     * Entfernt den Listener {@code onCommentCountChangedListener}
     * @param onCommentCountChangedListener listener
     */
    void removeOnCommentCountChangedListener(OnCommentCountChangedListener onCommentCountChangedListener);

    /**
     * Asynchroner Aufruf. Veröffentlicht ein Photo {@code imageBytes} über den Server. <br>
     * Verwenden Sie die Methode {@link BitmapUtils#bitmapToBytes(Bitmap)} um ein Bitmap in ein Byte Array zu konvertieren. <br>
     * Wenn ein Photo hochgeladen werden konnte, wird die Methode {@link OnPhotoUploadListener#onPhotoUploaded(Photo)} aufgerufen. <br>
     * Schlägt das Hochladen des Photos fehl, wird die Methode {@link OnPhotoUploadListener#onPhotoUploadFailed(HttpError)} aufgerufen. <br>
     * Das neue Photo wird zusätzlich über den Listener {@link OnNewPhotoReceivedListener} zurück geliefert
     * @param imageBytes das Photo als Byte Array
     * @param description die Beschreibung zu dem Photo
     * @throws IOException
     * @throws JSONException
     */
    void uploadPhoto(byte[] imageBytes, String description) throws IOException, JSONException;

    /**
     * Asynchroner Aufruf. <br>
     * Lädt die erste Seite von Photos aus dem Stream. <br>
     * Konnten die Photos geladen werden, wird die Methode {@link OnPhotosReceivedListener#onPhotosReceived(PhotoQueryResult)} aufgerufen. <br>
     * Schlägt das Laden der Photos fehl, wird die Methode {@link OnPhotosReceivedListener#onReceivePhotosFailed(HttpError)} aufgerufen. <br>
     * Wenn <b>Photos</b> in der App <b>bereits angezeigt</b> werden und keine neueren Photos verfügbar sind, wird die Methode {@link OnPhotosReceivedListener#onNoNewPhotosAvailable()} aufgerufen. <br>
     */
    void loadPhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Lädt eine weitere Seite von Photos aus dem Stream. <br>
     * Konnten die Photos geladen werden, wird die Methode {@link OnPhotosReceivedListener#onPhotosReceived(PhotoQueryResult)} aufgerufen. <br>
     * Schlägt das Laden der Photos fehl, wird die Methode {@link OnPhotosReceivedListener#onReceivePhotosFailed(HttpError)} aufgerufen. <br>
     */
    void loadMorePhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Serverseitige Suche von Photos anhand der Photobeschreibung {@code queryPhotoDescription}. <br>
     * Das Ergebnis ist die erste Seite der Suche. <br>
     * Wenn die Suche nach Photos ausgeführt werden konnte, wird die Methode {@link OnSearchedPhotosReceivedListener#onSearchedPhotosReceived(PhotoQueryResult)} aufgerufen (auch wenn die Suche keine passenden Photos liefert). <br>
     * Wenn die Suche nach Photos einen Fehler verursacht hat, wird die Methode {@link OnSearchedPhotosReceivedListener#onReceiveSearchedPhotosFailed(String, HttpError)} <br>
     * Um weitere Ergebnisse der Suche abzurufen, muss die Methode {@link IPhotoStreamClient#searchMorePhotos()} aufgerufen werden.
     * @param queryPhotoDescription Beschreibung zu dem Photo
     */
    void searchPhotos(String queryPhotoDescription);

    /**
     * Asynchroner Aufruf. <br>
     * Lädt die nächste Seite aus dem vorherigen Suchergebnis. <br>
     * Wenn weitere Photos zu einer vorherigen Suche geladen werden konnten, wird die Methode {@link OnSearchedPhotosReceivedListener#onSearchedPhotosReceived(PhotoQueryResult)} aufgerufen. <br>
     * Wenn allerdings ein Fehler aufgetreten ist , wird die Methode {@link OnSearchedPhotosReceivedListener#onReceiveSearchedPhotosFailed(String, HttpError)} <br>
     */
    void searchMorePhotos();

    /**
     * Asynchroner Aufruf. <br>
     * Löscht ein Photo von dem Server <br>
     * Wenn das Photo gelöscht werden konnte, wird die Methode {@link OnPhotoDeletedListener#onPhotoDeleted(int)} aufgerufen. <br>
     * Wenn das Photo nicht gelöscht werden konnte, wird die Methode {@link OnPhotoDeletedListener#onPhotoDeleteFailed(int, HttpError)} aufgerufen.
     * @param photoId id des Photos
     */
    void deletePhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Favorisiert ein Photo mit der id {@code photoId} <br>
     * Wenn das Photo favorisiert wurde, wird die Methode {@link OnPhotoFavoredListener#onPhotoFavored(int)} aufgerufen. <br>
     * Trat bei der Aktion jedoch ein Fehler auf, wird die Methode {@link OnPhotoFavoredListener#onFavoringPhotoFailed(int, HttpError)} aufgerufen. <br>
     * @param photoId id des Photos
     */
    void favoritePhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Entfavorisiert ein Photo mit der id {@code photoId} <br>
     * Wenn ein Photo entfavorisiert wurde, wird die Methode {@link OnPhotoFavoredListener#onPhotoUnfavored(int)} aufgerufen. <br>
     * Trat bei der Aktion jedoch ein Fehler auf, wird die Methode {@link OnPhotoFavoredListener#onFavoringPhotoFailed(int, HttpError)} aufgerufen. <br>
     * @param photoId id des Photos
     */
    void unfavoritePhoto(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Fordert alle Kommentare zu dem Photo mit der id {@code photoId} vom Server an. <br>
     * Wenn die Kommentare geladen werden konnten, wird die Methode {@link OnCommentsReceivedListener#onCommentsReceived(int, List)} aufgerufen. <br>
     * Wenn die Kommentare <b>nicht</b> geladen werden konnten, wird die Methode {@link OnCommentsReceivedListener#onReceiveCommentsFailed(int, HttpError)} (int, List)} aufgerufen. <br>
     * @param photoId id des Photos
     */
    void loadComments(int photoId);

    /**
     * Asynchroner Aufruf. <br>
     * Sendet einen Kommentar zu einem Photo an den Server. Der Kommentar wird zum Photo mit der übergebenen Id {@code photoId} gespeichert. <br>
     * Wenn der Kommentar gesendet werden konnte, wird die Methode {@link OnNewCommentReceivedListener#onNewCommentReceived(Comment)} aufgerufen. <br>
     * Schlägt das Senden des Kommentars an den Server fehl, wird die Methode {@link OnCommentUploadFailedListener#onCommentUploadFailed(HttpError)} aufgerufen <br>
     * @param photoId id des Photos
     * @param comment Kommentar zu dem Photo
     */
    void uploadComment(int photoId, String comment);

    /**
     * Asynchroner Aufruf. <br>
     * Löscht einen Kommentar von dem Server mit der id {@code commentId} <br>
     * Wenn der Kommentar gelöscht werden konnte, wird die Methode {@link OnCommentDeletedListener#onCommentDeleted(int)} aufgerufen. <br>
     * Schlägt das Löschen des Kommentars fehl, wird die Methode {@link OnCommentDeletedListener#onCommentDeleteFailed(int, HttpError)} aufgerufen. <br>
     * @param commentId Id des Kommentars
     */
    void deleteComment(int commentId);

    /**
     * Synchroner Aufruf. <br>
     * Mit dieser Methode kann abgefragt werden, ob aktuell ein Request zu einer bestimmten Kategorie {@link RequestType}, momentan verarbeitet wird.
     * @param requestType Typ des Requests ({@link RequestType})
     * @return {@code true}, wenn mindestens ein Request aus der angegebenen Kategorie verarbeitet wird, ansonsten {@code false}
     */
    boolean hasOpenRequestOfType(RequestType requestType);
}
