# photostream-tools [![Build Status](https://travis-ci.org/aschattney/photostream-tools.svg?branch=master)](https://travis-ci.org/aschattney/photostream-tools)

## Gradle

```gradle
compile 'hochschuledarmstadt.photostream_tools:photostream-tools:0.0.15'
```

## Architektur

<a href="https://github.com/aschattney/photostream-tools/blob/master/architecture.png"><img src="https://github.com/aschattney/photostream-tools/blob/master/architecture.png" align="center" height="500" ></a>

## Datenmodell

### Photo

Methode | Beschreibung
--------- | ------------
`String getId()`   | Eindeutige Id des Photos
`String getDescription()` | Beschreibung zu dem Photo
`boolean isLiked()` | Liefert zurück ob das Photo vom Nutzer geliked wurde
`boolean isDeletable()` | Wenn der Nutzer der Urheber des Photos ist, dann kann er das Photo löschen
`String getImageFilePath()`| Liefert den absoluten Dateipfad zurück unter dem das Photo auf dem Gerät abgespeichert ist

### Comment

Methode | Beschreibung
--------- | ------------
`String getId()` | Eindeutige Id des Kommentars
`String getPhotoId()` | Liefert die zum Kommentar zugehörige Photo Id
`String getMessage()` | Liefert den Inhalt des Kommentars
`boolean isDeletable()` | Wenn der Nutzer der Urheber des Kommentars ist, dann kann er diesen Kommentar löschen

## QueryResult

### PhotoQueryResult

Methode | Beschreibung
--------- | ------------
`int getPage()` | Gibt die aktuelle Seite des Streams zurück
`List<Photo> getPhotos()` | Gibt die eine List von Photos zurück, die in der Seite des Streams enthalten sind

### CommentQueryResult

Methode | Beschreibung
--------- | ------------
`int getPhotoId()` | Gibt die Photo Id zurück, für die Kommentare abgerufen wurden
`List<Photo> getComments()` | Gibt die Liste von Kommentaren zurück, die zu einem Photo gehören


### IPhotoStreamClient

Über dieses Interface kann mit dem Server oder der lokalen Datenbank kommuniziert werden.
* Hochladen von Photos
* Abrufen von Photos
* Liken von Photos
* Photos kommentieren
* Kommentare zu einem Photo abrufen
* Senden von Kommentaren
* Suchen von Photos

### Callbacks

Interface | Verwendung
--------- | ------------
<a href="#OnPhotosResultListener">OnPhotosResultListener</div>   | Abrufen des Photostreams
<a href="#OnPhotoLikeListener">OnPhotoLikeListener</div>      | Liken oder Disliken eines Photos
<a href="#OnCommentsResultListener">OnCommentsResultListener</div> | Abrufen von Kommentaren zu einem Photo, Senden von Kommentaren
<a href="#OnPhotoUploadListener">OnPhotoUploadListener</div>    | Veröffentlichen eines Photos
<a href="#OnSearchPhotosResultListener">OnSearchPhotosResultListener</div>   | Suchen im Stream nach Photos

<div id="#OnPhotosResultListener"></div>
#### OnPhotosResultListener
Methode | Beschreibung
--------- | ------------
  `void onPhotosReceived(PhotoQueryResult result)` | Wird aufgerufen wenn das Ergebnis zu dem Funktionsaufruf von `IPhotoStreamClient.getPhotos()` geladen wurde
  `void onReceivePhotosFailed(HttpResult httpResult)` | Wird aufgerufen wenn ein Fehler aufgetreten ist
  `void onNewPhotoReceived(Photo photo)` | Wird aufgerufen wenn ein eigenes Photo erfolgreich gesendet wurde, oder ein anderer Nutzer ein neues Photo gesendet hat
  `void onPhotoDeleted(int photoId)` | Wird aufgerufen wenn eigenes Photo erfolgreich gelöscht worden ist oder ein anderer Nutzer sein Photo gelöscht hat
  `void onPhotoDeleteFailed(int photoId, HttpResult httpResult)` | Wird aufgerufen wenn beim Löschen eines Photos ein Fehler aufgetreten ist

<div id="#OnPhotoLikeListener"></div>
#### OnPhotoLikeListener
Methode | Beschreibung
--------- | ------------
  `void onPhotoLiked(int photoId)` | Wird aufgerufen wenn ein Photo erfolgreich geliked wurde
  `void onPhotoDisliked(int photoId)` | Wird aufgerufen wenn ein Photo erfolgreich disliked wurde
  `void onPhotoLikeFailed(int photoId, HttpResult httpResult)` | Wird aufgerufen wenn ein like bzw dislike fehlschlägt

<div id="#OnCommentsResultListener"></div>
### OnCommentsResultListener
Methode | Beschreibung
--------- | ------------
  `void onGetComments(int photoId, List<Comment> comments)` | Wird aufgerufen wenn Kommentare zu einem Photo geladen wurden
  `void onGetCommentsFailed(int photoId, HttpResult httpResult)` | Wird aufgerufen wenn dabei ein Fehler aufgetreten ist
  `void onCommentDeleted(int commentId)` | Wird aufgerufen wenn eigener Kommentar erfolgreich gelöscht worden ist oder ein anderer Nutzer seinen Kommentar gelöscht hat
  `void onCommentDeleteFailed(int commentId, HttpResult httpResult)` | Wird aufgerufen wenn ein eigener Kommentar nicht gelöscht werden konnte
  `void onNewComment(Comment comment)` | Wird aufgerufen wenn ein eigener Kommentar erfolgreich gesendet wurde, oder ein anderer Nutzer einen neuen Kommentar gesendet hat
  `void onSendCommentFailed(HttpResult httpResult)` | Wird aufgerufen wenn beim Senden eines Kommentars ein Fehler aufgetreten ist

<div id="#OnPhotoUploadListener"></div>
#### OnPhotoUploadListener

Methode | Beschreibung
--------- | ------------
  `void onPhotoUploaded(Photo photo)` | Wird aufgerufen wenn ein Photo erfolgreich an den Server gesendet wurde
  `void onPhotoUploadFailed(HttpResult httpResult)` | Wird aufgerufen wenn beim Senden eines Photos an den Server ein Fehler aufgetreten ist
  
<div id="#OnSearchPhotosResultListener"></div>
#### OnSearchPhotosResultListener
Methode | Beschreibung
--------- | ------------
  `void onSearchedPhotosReceived(PhotoQueryResult result)` | Wird aufgerufen wenn das Ergebnis zu dem Funktionsaufruf von `IPhotoStreamClient.searchPhotos(String query)` geladen wurde
  `void onReceiveSearchedPhotosFailed(String query, HttpResult httpResult)` | Wird aufgerufen wenn dabei ein Fehler aufgetreten ist
  `void onNewPhotoReceived(Photo photo)` | Wird aufgerufen wenn ein eigenes Photo erfolgreich gesendet wurde, oder ein anderer Nutzer ein neues Photo gesendet hat
  `void onPhotoDeleted(int photoId)` | Wird aufgerufen wenn eigenes Photo erfolgreich gelöscht worden ist oder ein anderer Nutzer sein Photo gelöscht hat
  `void onPhotoDeleteFailed(int photoId, HttpResult httpResult)` | Wird aufgerufen wenn beim Löschen eines Photos ein Fehler aufgetreten ist

## License

The MIT License (MIT)
Copyright (c) 2016 Andreas Schattney

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
