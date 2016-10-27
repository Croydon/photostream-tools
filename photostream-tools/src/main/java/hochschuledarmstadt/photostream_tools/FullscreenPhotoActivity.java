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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Bietet Unterstützung bei der Vollbildanzeige von Photos.
 * Bei Berührung des Bildschirms werden die NavigationBar und die StatusBar versteckt bzw. angezeigt.
 */
public abstract class FullscreenPhotoActivity extends PhotoStreamActivity{

    private static final float TOUCH_OFFSET = 10.f;
    private static final String KEY_SYSTEM_UI_VISIBLE = "KEY_SYSTEM_UI_VISIBLE";
    private static final String KEY_SCALE = "KEY_SCALE";

    private static final Handler handler = new Handler(Looper.getMainLooper());

    private float scale = 1.0f;

    float x, y;
    private boolean isFirstStart = true;
    private boolean systemUiVisible = false;
    private ImageViewAttacher imageViewAttacher;
    private OnImageViewZoomChangedListener zoomChangedListener;
    private final Runnable runnableZoomReset = new Runnable() {
        @Override
        public void run() {
            if (zoomChangedListener != null)
                zoomChangedListener.onImageViewZoomReset();
        }
    };
    private final Runnable runnableZoomedIn = new Runnable() {
        @Override
        public void run() {
            if (zoomChangedListener != null)
                zoomChangedListener.onImageViewZoomedIn();
        }
    };
    private boolean uiVisibilitySelfTriggered = false;
    private Runnable runnableShowSystemUi = new Runnable() {
        @Override
        public void run() {
            uiVisibilitySelfTriggered = true;
            if (Build.VERSION.SDK_INT >= 16) {
                getWindow().getDecorView().getRootView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow().getDecorView().getRootView().setSystemUiVisibility(0);
            }
        }
    };
    private Runnable runnableHideSystemUi = new Runnable() {
        @Override
        public void run() {
            onSystemUiHidden();
        }
    };

    /**
     * Wird aufgerufen, wenn die Statusbar und Navigationbar sichtbar sind.
     */
    protected abstract void onSystemUiVisible();

    /**
     * Wird aufgerufen, wenn die Statusbar und Navigationbar nicht sichtbar sind
     */
    protected abstract void onSystemUiHidden();

    protected void setImageViewZoomable(ImageView imageView, OnImageViewZoomChangedListener zoomChangedListener){

        if (imageViewAttacher != null){
            imageViewAttacher.setOnDoubleTapListener(null);
            imageViewAttacher.setOnScaleChangeListener(null);
            imageViewAttacher.cleanup();
            imageViewAttacher = null;
        }

        this.zoomChangedListener = zoomChangedListener;

        imageViewAttacher = new ImageViewAttacher(imageView);
        imageViewAttacher.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });
        imageViewAttacher.setOnScaleChangeListener(new PhotoViewAttacher.OnScaleChangeListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                handler.removeCallbacks(runnableZoomedIn);
                handler.removeCallbacks(runnableZoomReset);
                if (Math.abs(1.0f - scaleFactor) < 0.001){
                    handler.postDelayed(runnableZoomReset, 150);
                }else {
                    handler.postDelayed(runnableZoomedIn, 150);
                }
            }
        });
        imageViewAttacher.setScale(scale);
        imageViewAttacher.update();
    }

    @Override
    public void onBackPressed() {
        if (imageViewAttacher != null && Math.abs(1.0f - imageViewAttacher.getScale()) >= 0.001) {
            imageViewAttacher.setScale(1.0f, false);
            handler.removeCallbacks(runnableZoomedIn);
            handler.removeCallbacks(runnableZoomReset);
            runnableZoomReset.run();
        }else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            isFirstStart = false;
            systemUiVisible = savedInstanceState.getBoolean(KEY_SYSTEM_UI_VISIBLE);
            scale = savedInstanceState.getFloat(KEY_SCALE);
        }

        getWindow().getDecorView().getRootView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (!uiVisibilitySelfTriggered) {
                    handler.removeCallbacks(runnableHideSystemUi);
                    handler.removeCallbacks(runnableShowSystemUi);
                    if (isSystemUiVisible())
                        onSystemUiVisible();
                    else
                        onSystemUiHidden();
                }else{
                    uiVisibilitySelfTriggered = false;
                }
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        handleSystemUi();

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

        try {
            boolean handled = super.dispatchTouchEvent(ev);
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                x = ev.getRawX();
                y = ev.getRawY();
            }
            if (canBeInterpretedAsATap(ev, handled)) {
                boolean isVisible = isSystemUiVisible();
                if (ev.getAction() == MotionEvent.ACTION_UP) {
                    if (isVisible)
                        hideSystemUI();
                    else
                        showSystemUI();
                    handled = true;
                }
            }
            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                x = 0;
                y = 0;
            }

            return handled;

        }catch(Exception e){}

        return false;
    }

    private boolean canBeInterpretedAsATap(MotionEvent ev, boolean handled) {
        float diffX = Math.abs(ev.getRawX() - x);
        float diffY = Math.abs(ev.getRawY() - y);
        boolean result = !handled || isTouchOnZoomableImageView(ev, diffX, diffY);
        return result;
    }

    private boolean isTouchOnZoomableImageView(MotionEvent ev, float diffX, float diffY) {
        return imageViewAttacher != null && (ev.getSource() == imageViewAttacher.getImageView().getId()) && ev.getAction() == MotionEvent.ACTION_UP && diffX < TOUCH_OFFSET && diffY < TOUCH_OFFSET;
    }

    /**
     * Liefert zurück, ob Statusbar und Navigationbar zum aktuellen Zeitpunkt sichtbar sind oder nicht
     * @return {@code true, wenn beide Bars sichtbar sind, ansonsten {@code false}}
     */
    public boolean isSystemUiVisible() {
        int currentSystemUiVisibility = getWindow().getDecorView().getRootView().getSystemUiVisibility();
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
        outState.putFloat(KEY_SCALE, imageViewAttacher != null ? imageViewAttacher.getScale() : 1.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleSystemUi();
        isFirstStart = false;
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {

        handler.removeCallbacks(runnableShowSystemUi);
        handler.removeCallbacks(runnableHideSystemUi);

        uiVisibilitySelfTriggered = true;

        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
            getWindow().getDecorView().getRootView().setSystemUiVisibility(
                              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);

        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().getRootView().setSystemUiVisibility(
                              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            getWindow().getDecorView().getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        runnableHideSystemUi.run();
    }

    private void showSystemUI() {
        handler.removeCallbacks(runnableShowSystemUi);
        handler.removeCallbacks(runnableHideSystemUi);
        runnableShowSystemUi.run();
        onSystemUiVisible();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnableHideSystemUi);
        handler.removeCallbacks(runnableShowSystemUi);
        handler.removeCallbacks(runnableZoomReset);
        handler.removeCallbacks(runnableZoomedIn);
        if (imageViewAttacher != null)
            imageViewAttacher.cleanup();
        super.onDestroy();
    }

    protected interface OnImageViewZoomChangedListener {
        /**
         * Diese Methode wird aufgerufen, sobald das Bild nicht mehr gezoomt ist
         */
        void onImageViewZoomReset();
        /**
         * Diese Methode wird aufgerufen, wenn das Bild gezoomt wurde
         */
        void onImageViewZoomedIn();
    }

    private static class ImageViewAttacher extends PhotoViewAttacher {

        public ImageViewAttacher(ImageView imageView) {
            super(imageView);
        }

        public ImageViewAttacher(ImageView imageView, boolean zoomable) {
            super(imageView, zoomable);
        }

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            boolean result = super.onTouch(v, ev);
            ev.setSource(getImageView().getId());
            return true;
        }
    }

}
