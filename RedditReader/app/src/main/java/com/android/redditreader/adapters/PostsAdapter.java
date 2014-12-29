package com.android.redditreader.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.redditreader.R;
import com.android.redditreader.models.Post;

import java.util.ArrayList;

/**
 * Created by Manas on 29-12-2014.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private Context context;
    public ArrayList<Post> posts;

    public PostsAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.numCommentsTextView.setText(post.getNum_comments() + " comments");
        holder.scoreTextView.setText(post.getScore() + " points");
        holder.infoTextView.setText(post.getAuthor() + " • " +  post.getSubreddit() + " • " + post.getDomain());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView titleTextView;
        private TextView infoTextView;
        private TextView scoreTextView;
        private TextView numCommentsTextView;
        private ImageView thumbnailImageView;
        private ImageButton upvoteButton;
        private ImageButton downvoteButton;

        public PostViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_textview);
            infoTextView = (TextView) itemView.findViewById(R.id.info_textview);
            scoreTextView = (TextView) itemView.findViewById(R.id.score_textview);
            numCommentsTextView = (TextView) itemView.findViewById(R.id.num_comments_textview);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_imageview);
            upvoteButton = (ImageButton) itemView.findViewById(R.id.upvote_button);
            downvoteButton = (ImageButton) itemView.findViewById(R.id.downvote_button);
        }

        @Override
        public void onClick(View v) {
            // TODO handle card click event
        }
    }
}