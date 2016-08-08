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
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import hochschuledarmstadt.photostream_tools.model.BaseItem;

public abstract class PluginAlertDialog<T extends BaseItem & Parcelable, H extends RecyclerView.ViewHolder> extends Plugin<H, T> {

    private static final String KEY_PHOTO = "KEY_PHOTO";
    private static final String KEY_SELECTED_ITEM_IDS = "KEY_SELECTED_ITEM_IDS";
    private final AppCompatActivity activity;
    private final int dialogStyle;

    private T item;
    private AlertDialogBuilderProxy builder;
    private ArrayList<Integer> selectedIds = new ArrayList<>();

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

        Integer itemId = item.getId();

        if (!selectedIds.contains(itemId))
            selectedIds.add(itemId);
        else
            selectedIds.remove(itemId);

        notifyItemChanged(itemId);

    }

    private void notifyItemChanged(Integer itemId) {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItemAtPosition(i).getId() == itemId) {
                dontAnimate(itemId);
                adapter.notifyItemChanged(i, Boolean.FALSE);
                break;
            }
        }
    }

    private void showAlertDialogIfNecessary() {
        if (builder.alertDialog == null){
            builder.show();
        }else if(!builder.alertDialog.isShowing()){
            builder.alertDialog.show();
        }
    }

    @Override
    boolean onBindViewHolder(H viewHolder, int position) {
        int photoId = adapter.getItemAtPosition(position).getId();
        Integer itemId = Integer.valueOf(photoId);
        boolean selected = selectedIds.contains(itemId);
        View view = viewHolder.itemView.findViewById(getViewId());
        boolean previouslySelected = view.isSelected();
        view.setSelected(selected);
        return previouslySelected == selected;
    }

    @Override
    void saveInstanceState(Bundle bundle) {
        AlertDialog alertDialog = builder.alertDialog;
        if ((builder == null || alertDialog == null) || !alertDialog.isShowing()){
            item = null;
        }
        if (builder != null && alertDialog != null && alertDialog.isShowing()) {
            DialogInterface.OnDismissListener dismissListener = builder.getOnDismissListener().onDismissListener;
            if (dismissListener != null)
                dismissListener.onDismiss(alertDialog);
            builder.clear();
            alertDialog.dismiss();
        }
        builder.alertDialog = null;
        bundle.putParcelable(KEY_PHOTO, item);
        bundle.putIntegerArrayList(KEY_SELECTED_ITEM_IDS, selectedIds);
    }

    @Override
    void restoreInstanceState(Bundle bundle) {
        item = bundle.getParcelable(KEY_PHOTO);
        selectedIds = bundle.getIntegerArrayList(KEY_SELECTED_ITEM_IDS);
        if (item != null) {
            builder = new AlertDialogBuilderProxy(activity, dialogStyle);
            onCreateAlertDialog(builder, item);
            showAlertDialogIfNecessary();
        }
    }

    private class AlertDialogBuilderProxy extends AlertDialog.Builder {

        public AlertDialog alertDialog;
        private boolean called;
        private DelegateOnDismissListener onDismissListener;

        public AlertDialogBuilderProxy(Context context) {
            super(context);
        }

        public AlertDialogBuilderProxy(Context context, int theme) {
            super(context, theme);
        }

        public DelegateOnDismissListener getOnDismissListener() {
            return onDismissListener;
        }

        @Override
        public AlertDialog create() {
            if (!called)
                setOnDismissListener(null);
            called = true;
            alertDialog = super.create();
            return alertDialog;
        }

        @Override
        public AlertDialog show() {
            if (!called)
                setOnDismissListener(null);
            called = true;
            alertDialog = super.show();
            return alertDialog;
        }

        @Override
        public AlertDialog.Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            called = true;
            this.onDismissListener = new DelegateOnDismissListener(onDismissListener);
            return super.setOnDismissListener(this.onDismissListener);
        }

        void clear() {
            alertDialog.setOnDismissListener(null);
        }

        private class DelegateOnDismissListener implements DialogInterface.OnDismissListener {
            private final DialogInterface.OnDismissListener onDismissListener;

            public DelegateOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
                this.onDismissListener = onDismissListener;
            }

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Integer itemId = Integer.valueOf(item.getId());
                if (selectedIds.contains(itemId)) {
                    selectedIds.remove(itemId);
                    notifyItemChanged(itemId);
                }
                if (this.onDismissListener != null)
                    this.onDismissListener.onDismiss(dialogInterface);
            }
        }
    }

}
