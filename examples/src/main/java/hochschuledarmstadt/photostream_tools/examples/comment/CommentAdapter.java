package hochschuledarmstadt.photostream_tools.examples.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import hochschuledarmstadt.photostream_tools.adapter.BaseAdapter;
import hochschuledarmstadt.photostream_tools.examples.R;
import hochschuledarmstadt.photostream_tools.model.Comment;

/**
 * Created by Andreas Schattney on 12.03.2016.
 */
public class CommentAdapter extends BaseAdapter<CommentAdapter.CommentViewHolder, Comment>{

    private final Context context;

    public CommentAdapter(Context context) {
        super(new ArrayList<Comment>());
        this.context = context;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public CommentViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = getItemAtPosition(position);
        holder.textView.setText(comment.getMessage());
    }

}
