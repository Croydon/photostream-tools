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

package hochschuledarmstadt.photostream_tools.examples.examples.upload;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.RequestType;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoUploadListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;

public class PhotoUploadActivity extends PhotoStreamActivity implements OnPhotoUploadListener{

    private static final String TAG = PhotoUploadActivity.class.getName();
    private Button uploadButton;
    private Bitmap bitmap;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        // Button und EditText referenzieren
        findViews();

        // Beispielphoto
        Uri assetUri = Uri.parse("assets://architecture.png");

        // Bitmap laden
        loadBitmapAsync(assetUri, new OnBitmapLoadedListener() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap) {
                PhotoUploadActivity.this.bitmap = bitmap;
                // OnClickListener setzen für Button etc.
                initializeViews();
            }

            @Override
            public void onLoadBitmapError(IOException e) {
                Log.e(TAG, "Fehler beim Dekodieren des Bilds architecture.png", e);
            }
        });
    }

    private void findViews() {
        editText = (EditText) findViewById(R.id.commentEditText);
        uploadButton = (Button) findViewById(R.id.uploadButton);
    }

    private void initializeViews() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadButton.setEnabled(false);
                IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                byte[] imageBytes = BitmapUtils.bitmapToBytes(bitmap);
                try {
                    String description = editText.getText().toString().trim();
                    photoStreamClient.uploadPhoto(imageBytes, description);
                } catch (IOException e) {
                    Log.e(TAG, "error while sending photo to server", e);
                    uploadButton.setEnabled(true);
                } catch (JSONException e) {
                    Log.e(TAG, "error while encoding data to json", e);
                    uploadButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        BitmapUtils.recycleBitmap(bitmap);
        super.onDestroy();
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnPhotoUploadListener(this);
        if (savedInstanceState != null){
            boolean uploadRequestIsRunning = photoStreamClient.hasOpenRequestOfType(RequestType.UPLOAD_PHOTO);
            uploadButton.setEnabled(!uploadRequestIsRunning);
        }
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnPhotoUploadListener(this);
    }

    @Override
    public void onPhotoUploaded(Photo photo) {
        Toast.makeText(this, "Photo Uploaded", Toast.LENGTH_LONG).show();
        uploadButton.setEnabled(true);
    }

    @Override
    public void onPhotoUploadFailed(HttpError httpError) {
        Utils.showErrorInAlertDialog(this, "Photo Upload failed", httpError);
        uploadButton.setEnabled(true);
    }

}
