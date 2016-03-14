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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment implements OnServiceStateChangedListener {

    private boolean serviceDisconnectCalled;
    private Bundle refSavedInstanceState;

    protected abstract void onPhotoStreamServiceConnected(IPhotoStreamClient service, Bundle savedInstanceState);
    protected abstract void onPhotoStreamServiceDisconnected(IPhotoStreamClient service);

    private IPhotoStreamClient photoStreamClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.refSavedInstanceState = savedInstanceState;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((ServiceStateChangedNotifier)getActivity()).addOnServiceStateChangedListener(this);
    }

    @Override
    public void onDestroy() {
        ((ServiceStateChangedNotifier)getActivity()).removeOnServiceStateChangedListener(this);
        super.onDestroy();
    }

    protected boolean connectedToService(){
        return photoStreamClient != null;
    }

    @Override
    public void onServiceConnected(IPhotoStreamClient client) {
        photoStreamClient = client;
        onPhotoStreamServiceConnected(client, refSavedInstanceState);
    }

    @Override
    public void onServiceDisconnected(IPhotoStreamClient client) {
        if (!serviceDisconnectCalled) {
            serviceDisconnectCalled = true;
            onPhotoStreamServiceDisconnected(photoStreamClient);
            photoStreamClient = null;
        }
    }

    @Override
    public void onDestroyView() {
        if (!serviceDisconnectCalled){
            onServiceDisconnected(photoStreamClient);
            serviceDisconnectCalled = true;
        }
        refSavedInstanceState = null;
        super.onDestroyView();
    }

    public IPhotoStreamClient getPhotoStreamClient() {
        return photoStreamClient;
    }
}
