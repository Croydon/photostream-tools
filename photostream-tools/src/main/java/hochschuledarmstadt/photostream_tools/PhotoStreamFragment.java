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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.UUID;

import hochschuledarmstadt.photostream_tools.callback.OnPhotosReceivedListener;

/**
 * Fragmente erhalten durch Erben von dieser Klasse Zugriff auf das Interface {@link IPhotoStreamClient}
 */
public abstract class PhotoStreamFragment extends Fragment implements OnServiceStateChangedListener , PhotoStreamFragmentActivity.OnBackPressedListener{

    private boolean serviceDisconnectCalled;
    private Bundle refSavedInstanceState;

    protected abstract void onPhotoStreamServiceConnected(IPhotoStreamClient service, Bundle savedInstanceState);
    protected abstract void onPhotoStreamServiceDisconnected(IPhotoStreamClient service);

    private PhotoStreamClientDelegate photoStreamClient;

    private static final String KEY_FRAGMENT_ID = "KEY_FRAGMENT_ID";

    private String fragmentId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.refSavedInstanceState = savedInstanceState;
        fragmentId = savedInstanceState == null ? UUID.randomUUID().toString() : savedInstanceState.getString(KEY_FRAGMENT_ID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FRAGMENT_ID, fragmentId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ServiceStateChangedNotifier)getActivity()).addOnServiceStateChangedListener(this);
        ((OnBackPressedNotifier)getActivity()).addOnBackPressedListener(this);
    }

    @Override
    public void onDestroy() {
        ((ServiceStateChangedNotifier)getActivity()).removeOnServiceStateChangedListener(this);
        ((OnBackPressedNotifier)getActivity()).removeOnBackPressedListener(this);
        super.onDestroy();
    }

    /**
     * Liefert zurück ob das Fragment mit dem Service verbunden ist
     * @return {@code true}, wenn das Fragment mit dem Service verbunden ist, ansonsten {@code false}
     */
    protected boolean isConnectedToService(){
        return photoStreamClient != null;
    }

    /**
     * Wenn das Fragment mit dem Service verbunden ist, dann wird die Instanz des Clients zurück geliefert, ansonsten {@code null}
     * @return Instanz des Clients oder {@code null}
     */
    public IPhotoStreamClient getPhotoStreamClient() {
        return photoStreamClient;
    }

    @Override
    public void onServiceConnected(IPhotoStreamClient client) {
        photoStreamClient = (PhotoStreamClientDelegate) client;
        onPhotoStreamServiceConnected(client, refSavedInstanceState);
    }

    @Override
    public String getPhotoStreamFragmentId() {
        return fragmentId;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isConnectedToService()){
            if (getActivity().isChangingConfigurations() && photoStreamClient.hasOnPhotosReceivedListenerRegistered()) {
                photoStreamClient.setShouldReloadFirstPageOfPhotosFromCache(Boolean.FALSE);
            }else if(getActivity().isFinishing() || isRemoving())
                photoStreamClient.clearShouldReloadFirstPageOfPhotosFromCache();
        }
    }

    @Override
    public void onServiceDisconnected() {
        internalNotifyDisconnectFromService(photoStreamClient);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroyView() {
        internalNotifyDisconnectFromService(photoStreamClient);
        refSavedInstanceState = null;
        super.onDestroyView();
    }

    private void internalNotifyDisconnectFromService(IPhotoStreamClient client) {
        if (client != null && !serviceDisconnectCalled){
            serviceDisconnectCalled = true;
            onPhotoStreamServiceDisconnected(client);
        }
        photoStreamClient = null;
    }

    public void loadBitmapAsync(File file, final PhotoStreamActivity.OnBitmapLoadedListener listener){
        ((PhotoStreamFragmentActivity)getActivity()).loadBitmapAsync(file, listener);
    }

    public void loadBitmapAsync(Uri uri, final PhotoStreamActivity.OnBitmapLoadedListener listener){
        ((PhotoStreamFragmentActivity)getActivity()).loadBitmapAsync(uri, listener);
    }

    void loadBitmapAsync(String assetFileName, final PhotoStreamActivity.OnBitmapLoadedListener listener){
        ((PhotoStreamFragmentActivity)getActivity()).loadBitmapAsync(assetFileName, listener);
    }

    @Override
    public void startActivity(Intent intent) {
        if (photoStreamClient != null && photoStreamClient.hasOnPhotosReceivedListenerRegistered())
            intent.putExtra("parent", fragmentId);
        super.startActivity(intent);
    }

}
