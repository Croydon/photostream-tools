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

package hochschuledarmstadt.photostream_tools.examples.comment;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hochschuledarmstadt.photostream_tools.adapter.BaseCommentAdapter;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Comment;

public class CommentAdapter extends BaseCommentAdapter<CommentAdapter.CommentViewHolder> {

    public CommentAdapter() { }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CommentViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    /*
            Wird aufgerufen, wenn ein neues Layout benötigt wird.
            Es werden nur so viele ViewHolder instanziert wie Elemente in der Liste angezeigt werden können.
            Sind also beispielsweise auf einem Handy in der Liste immer nur 3 Elemente sichtbar, wird diese Methode 3x aufgerufen.
            Anschließend werden die erzeugten ViewHolder, wenn möglich, für andere Elemente in der Liste wiederverwendet.
            Beispielsweise beim Scrollen:

            -----------------------------               -----------------------------
            |        ViewHolder 1       |               |        ViewHolder 2       |
            |         Element 1         |               |         Element 2         |
            -----------------------------               -----------------------------
            |        ViewHolder 2       |      =>       |        ViewHolder 3       |
            |         Element 2         |  Scroll um    |         Element 3         |
            -----------------------------  1 Element    -----------------------------
            |        ViewHolder 3       |  nach unten   |        ViewHolder 1       |
            |         Element 3         |               |         Element 4         |
            -----------------------------               -----------------------------

            ViewHolder 1 wird also für Element 4 wiederverwendet! Hierbei wird die Methode
            "onBindViewHolder", mit den Argumenten ViewHolder 1 und position 4, aufgerufen
        */
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.comment_item, parent, false);
        CommentViewHolder viewHolder = new CommentViewHolder(v);
        return viewHolder;
    }

    /*
        Wird intern von der RecyclerView aufgerufen, um den Inhalt einer View,
        mit Informationen aus dem Element an der "position", zu aktualisieren
     */
    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Comment comment = getItemAtPosition(position);
        holder.textView.setText(comment.getMessage());
    }

}
