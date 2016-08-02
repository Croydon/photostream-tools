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


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.view.MotionEvent;
import android.view.View;

/**
 * Bietet Unterstützung bei der Vollbildanzeige von Photos.
 * Bei Berührung des Bildschirms werden die NavigationBar und die StatusBar versteckt bzw. angezeigt.
 */
public abstract class FullscreenPhotoActivity extends PhotoStreamActivity{

    private static final String KEY_SYSTEM_UI_VISIBLE = "KEY_SYSTEM_UI_VISIBLE";

    private boolean isFirstStart = true;
    private boolean systemUiVisible = false;

    private static final Handler handler = new Handler(Looper.getMainLooper());

    protected abstract void onSystemUiVisible();
    protected abstract void onSystemUiHidden();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            isFirstStart = false;
            systemUiVisible = savedInstanceState.getBoolean(KEY_SYSTEM_UI_VISIBLE);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (savedInstanceState == null) {
                watchForEnterTransition();
            }
            watchForExitTransition();
        }
    }

    private void handleSystemUi() {
        if (isFirstStart){
            hideSystemUI();
        }else if (!systemUiVisible){
            hideSystemUI();
        }else{
            showSystemUI();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleSystemUi();
    }

    @Override
    protected void onPause() {
        systemUiVisible = isSystemUiVisible();
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isVisible = isSystemUiVisible();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isVisible)
                hideSystemUI();
            else
                showSystemUI();
            return true;
        }
        return true;
    }

    /**
     * Liefert zurück, ob Statusbar und Navigationbar sichtbar sind oder nicht
     * @return {@code true, wenn beide Bars sichtbar sind, ansonsten {@code false}}
     */
    public boolean isSystemUiVisible() {
        int currentSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= 16) {
            int isVisible = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            return currentSystemUiVisibility == isVisible;
        }else if (currentSystemUiVisibility == 0){
            return true;
        }else{
            return false;
        }
    }

    private void watchForExitTransition(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementExitTransition().addListener(new Transition.TransitionListener() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTransitionStart(Transition transition) {
                    getWindow().getSharedElementExitTransition().removeListener(this);
                    showSystemUI();
                }

                @Override
                public void onTransitionEnd(Transition transition) {

                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }

    private void watchForEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTransitionEnd(Transition transition) {
                    getWindow().getSharedElementEnterTransition().removeListener(this);
                    hideSystemUI();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SYSTEM_UI_VISIBLE, isSystemUiVisible());
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleSystemUi();
        isFirstStart = false;
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onSystemUiHidden();
            }
        }, 500);

    }

    private void showSystemUI() {
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }else{
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        handler.removeCallbacksAndMessages(null);
        onSystemUiVisible();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
