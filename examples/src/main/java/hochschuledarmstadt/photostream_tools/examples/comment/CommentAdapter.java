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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hochschuledarmstadt.photostream_tools.adapter.SimpleCommentAdapter;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Comment;

public class CommentAdapter extends SimpleCommentAdapter<CommentAdapter.CommentViewHolder>{

    public CommentAdapter() { }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CommentViewHolder(View itemView) {
            super(itemView);
            // TextView referenzieren
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    /*
        Wird aufgerufen, wenn ein neues Layout benötigt wird.
        Es werden nur so viele ViewHolder instanziert wie Views in der Liste angezeigt werden können.
        Sind also in der Liste beispielsweise immer nur 5 Elemente sichtbar, wird diese Methode 5x aufgerufen.
        Anschließend werden die erzeugten ViewHolder, wenn möglich, für andere Elemente in der Liste wiederverwendet (z.B) beim scrollen)
    */
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(v);
    }

    /*
        Wird aufgerufen, wenn ein Layout in der Liste aktualisiert werden muss.
        Bedeutet, dass ein bestehender ViewHolder ein anderes Element an der "position" in der Liste repräsentieren soll.
     */
    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = getItemAtPosition(position);
        holder.textView.setText(comment.getMessage());
    }

}
