package com.android.redditreader.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.redditreader.R;
import com.android.redditreader.models.Post;
import com.android.redditreader.utils.Globals;
import com.android.redditreader.utils.Helpers;

import java.util.ArrayList;

/**
 * Created by Manas on 29-12-2014.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private final String TAG = PostsAdapter.class.getSimpleName();
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
        holder.numCommentsTextView.setText(post.getNum_comments() == 1 ? post.getNum_comments() + " comment" : post.getNum_comments() + " comments");
        holder.scoreTextView.setText(post.getScore() == 1 ? post.getScore() + " point" : post.getScore() + " points");
        holder.infoTextView.setText(post.getAuthor() + " • " + post.getCreated() + " • " +  post.getSubreddit() + " • " + post.getDomain());
        Helpers.displayThumbnail(post.getThumbnail(), holder.thumbnailImageView);
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
        private ImageButton socialShareButton;
        private ImageButton moreOptionsButton;

        public PostViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_textview);
            infoTextView = (TextView) itemView.findViewById(R.id.info_textview);
            scoreTextView = (TextView) itemView.findViewById(R.id.score_textview);
            numCommentsTextView = (TextView) itemView.findViewById(R.id.num_comments_textview);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_imageview);
            upvoteButton = (ImageButton) itemView.findViewById(R.id.upvote_button);
            downvoteButton = (ImageButton) itemView.findViewById(R.id.downvote_button);
            socialShareButton = (ImageButton) itemView.findViewById(R.id.social_share_button);
            moreOptionsButton = (ImageButton) itemView.findViewById(R.id.more_options_button);

            socialShareButton.setOnClickListener(this);
            moreOptionsButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Post selectedPost = posts.get(getPosition());

            switch (v.getId()) {
                case R.id.social_share_button:
                    AlertDialog shareDialog = new AlertDialog.Builder(context)
                            .setItems(R.array.post_share_dialog_list_items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            // Share link
                                            Helpers.socialShareLink(context, selectedPost.getUrl());
                                            break;
                                        case 1:
                                            // Share comments
                                            Helpers.socialShareLink(context, Globals.BASE_API_URL + selectedPost.getPermalink());
                                            break;
                                    }
                                }
                            })
                            .create();
                    shareDialog.show();
                    break;
                case R.id.more_options_button:
                    AlertDialog moreOptionsDialog = new AlertDialog.Builder(context)
                            .setItems(R.array.post_options_dialog_list_items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            // View subreddit
                                            Toast.makeText(context, selectedPost.getSubreddit(), Toast.LENGTH_SHORT).show();
                                            break;
                                        case 1:
                                            // View OP's profile
                                            Toast.makeText(context, selectedPost.getAuthor(), Toast.LENGTH_SHORT).show();
                                            break;
                                        case 2:
                                            // View link in browser
                                            Helpers.viewURLInBrowser(context, selectedPost.getUrl());
                                            break;
                                        case 3:
                                            // View comments in browser
                                            Helpers.viewURLInBrowser(context, Globals.BASE_API_URL + selectedPost.getPermalink());
                                            break;
                                    }
                                }
                            })
                            .create();
                    moreOptionsDialog.show();
                    break;
            }
        }
    }
}