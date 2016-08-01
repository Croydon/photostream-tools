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

package hochschuledarmstadt.photostream_tools.examples.viewpager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hochschuledarmstadt.photostream_tools.BitmapUtils;
import hochschuledarmstadt.photostream_tools.IPhotoStreamClient;
import hochschuledarmstadt.photostream_tools.PhotoStreamFragment;
import hochschuledarmstadt.photostream_tools.adapter.BaseFragmentPagerAdapter;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.callback.OnCommentsReceivedListener;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.examples.Utils;
import hochschuledarmstadt.photostream_tools.examples.comment.CommentAdapter;
import hochschuledarmstadt.photostream_tools.model.Comment;
import hochschuledarmstadt.photostream_tools.model.HttpError;
import hochschuledarmstadt.photostream_tools.model.Photo;

public class ContentFragment extends PhotoStreamFragment implements OnCommentsReceivedListener {

    public static final String KEY_POSITION = BaseFragmentPagerAdapter.KEY_POSITION;
    private static final String KEY_COMMENTS = "KEY_COMMENTS";
    private ImageView imageView;
    private int position;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private Photo photo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            position = getArguments().getInt(KEY_POSITION);
        }else {
            position = savedInstanceState.getInt(KEY_POSITION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, position);
        outState.putBundle(KEY_COMMENTS, adapter.saveInstanceState());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewpager_item, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new CommentAdapter();

        recyclerView.setAdapter(adapter);

        imageView = (ImageView) getView().findViewById(R.id.imageView);

        photo = ((ViewPagerActivity)getActivity()).getPhoto(position);

        try {
            Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(photo.getImageFile());
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle(KEY_COMMENTS);
            adapter.restoreInstanceState(bundle);
        }
    }

    @Override
    protected void onPhotoStreamServiceConnected(IPhotoStreamClient service, Bundle savedInstanceState) {
        service.addOnCommentsReceivedListener(this);
        photo = ((ViewPagerActivity)getActivity()).getPhoto(position);
        if (savedInstanceState == null)
            service.loadComments(photo.getId());
    }

    @Override
    protected void onPhotoStreamServiceDisconnected(IPhotoStreamClient service) {
        service.removeOnCommentsReceivedListener(this);
    }

    @Override
    public void onCommentsReceived(int photoId, List<Comment> comments) {
        if (photoId == photo.getId())
            adapter.set(comments);
    }

    @Override
    public void onDestroyView() {
        BitmapUtils.recycleBitmapFromImageView(imageView);
        super.onDestroyView();
    }

    @Override
    public void onReceiveCommentsFailed(int photoId, HttpError httpError) {
        Utils.showErrorInAlertDialog(getActivity(), "Kommentare konnten nicht geladen werden", httpError);
    }

    @Override
    public void onRequestStarted() {

    }

    @Override
    public void onRequestFinished() {

    }
}
