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

package hochschuledarmstadt.photostream_tools.examples.like;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.callback.OnPhotoLikeListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.HttpResult;

public class LikeActivity extends PhotoStreamActivity implements OnPhotoLikeListener {

    private static final int PHOTO_ID = 1;
    private static final String KEY_LIKEBUTTON_TEXT = "KEY_LIKEBUTTON_TEXT";
    private static final String KEY_TEXTVIEW_TEXT = "KEY_TEXTVIEW_TEXT";
    private Button likeButton;
    private TextView textView;

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient photoStreamClient, Bundle savedInstanceState) {
        photoStreamClient.addOnPhotoLikeListener(this);
        if (savedInstanceState == null){
            if (!photoStreamClient.hasUserLikedPhoto(PHOTO_ID)){
                likeButton.setText(R.string.like);
                textView.setText(R.string.status_not_liked);
            }else{
                likeButton.setText(R.string.dislike);
                textView.setText(R.string.status_liked);
            }
        }
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient photoStreamClient) {
        photoStreamClient.removeOnPhotoLikeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        textView = (TextView) findViewById(R.id.statusTextView);
        likeButton = (Button) findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IPhotoStreamClient photoStreamClient = getPhotoStreamClient();
                if (!photoStreamClient.hasUserLikedPhoto(PHOTO_ID))
                    photoStreamClient.likePhoto(PHOTO_ID);
                else
                    photoStreamClient.dislikePhoto(PHOTO_ID);
            }
        });

        if (savedInstanceState != null){
            likeButton.setText(savedInstanceState.getString(KEY_LIKEBUTTON_TEXT));
            textView.setText(savedInstanceState.getString(KEY_TEXTVIEW_TEXT));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_LIKEBUTTON_TEXT, likeButton.getText().toString());
        outState.putString(KEY_TEXTVIEW_TEXT, textView.getText().toString());
    }

    @Override
    public void onPhotoLiked(int photoId) {
        textView.setText(R.string.status_liked);
        likeButton.setText(R.string.dislike);
    }

    @Override
    public void onPhotoDisliked(int photoId) {
        textView.setText(R.string.status_not_liked);
        likeButton.setText(R.string.like);
    }

    @Override
    public void onPhotoLikeFailed(int photoId, HttpResult httpResult) {
        Toast.makeText(this, httpResult.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public void onDismissProgressDialog() {
        findViewById(R.id.progressCircle).setVisibility(ProgressBar.GONE);
    }
}
