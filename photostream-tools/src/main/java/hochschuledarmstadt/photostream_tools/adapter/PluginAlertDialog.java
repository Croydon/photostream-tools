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

package hochschuledarmstadt.photostream_tools.adapter;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import hochschuledarmstadt.photostream_tools.model.BaseItem;

public abstract class PluginAlertDialog<T extends BaseItem & Parcelable, H extends RecyclerView.ViewHolder> extends Plugin<H, T> {

    private static final String PHOTO_KEY = "PHOTO_KEY";
    private final AppCompatActivity activity;
    private final int dialogStyle;

    private BaseAdapter<H, T> adapter;
    private T item;
    private AlertDialogBuilderProxy builder;

    /**
     * Wird aufgerufen, wenn der Dialog erzeugt werden soll. Hierzu muss das übergebene {@code builder} <br>
     * Objekt verwendet werden.
     * @param builder
     * @param item
     */
    protected abstract void onCreateAlertDialog(AlertDialog.Builder builder, T item);

    public PluginAlertDialog(AppCompatActivity activity, @StyleRes int dialogStyle){
        this.activity = activity;
        this.dialogStyle = dialogStyle;
    }


    @Override
    void trigger(H viewHolder, View v, T item) {
        this.item = item;
        builder = new AlertDialogBuilderProxy(activity, dialogStyle);
        onCreateAlertDialog(builder, item);
        showAlertDialogIfNecessary();
    }

    private void showAlertDialogIfNecessary() {
        if (builder.alertDialog == null){
            builder.show();
        }else if(!builder.alertDialog.isShowing()){
            builder.alertDialog.show();
        }
    }

    @Override
    void onBindViewHolder(H viewHolder, int position) {

    }

    @Override
    void saveInstanceState(Bundle bundle) {
        if ((builder == null || builder.alertDialog == null) || !builder.alertDialog.isShowing()){
            item = null;
        }
        if (builder != null && builder.alertDialog != null)
            builder.alertDialog.dismiss();
        bundle.putParcelable(PHOTO_KEY, item);
    }

    @Override
    void restoreInstanceState(Bundle bundle) {
        item = bundle.getParcelable(PHOTO_KEY);
        if (item != null) {
            builder = new AlertDialogBuilderProxy(activity, dialogStyle);
            onCreateAlertDialog(builder, item);
            showAlertDialogIfNecessary();
        }
    }

    @Override
    void setAdapter(BaseAdapter<H, T> adapter) {
        this.adapter = adapter;
   }

    private static class AlertDialogBuilderProxy extends AlertDialog.Builder {

        public AlertDialog alertDialog;

        public AlertDialogBuilderProxy(Context context) {
            super(context);
        }

        public AlertDialogBuilderProxy(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public AlertDialog create() {
            alertDialog = super.create();
            return alertDialog;
        }

        @Override
        public AlertDialog show() {
            alertDialog = super.show();
            return alertDialog;
        }
    }

}
