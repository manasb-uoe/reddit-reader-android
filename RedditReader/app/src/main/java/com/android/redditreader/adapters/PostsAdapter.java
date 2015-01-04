package com.android.redditreader.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.redditreader.R;
import com.android.redditreader.background_tasks.VoteTask;
import com.android.redditreader.models.Post;
import com.android.redditreader.utils.Globals;
import com.android.redditreader.utils.Helpers;
import com.manas.asyncimageloader.AsyncImageLoader;

import java.util.ArrayList;

/**
 * Created by Manas on 29-12-2014.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private final String TAG = PostsAdapter.class.getSimpleName();
    private Context context;
    public ArrayList<Post> posts;
    private Resources res;

    public PostsAdapter(Context context, ArrayList<Post> posts) {
        this.context = context;
        this.posts = posts;
        res = context.getResources();
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
        Helpers.displayPostThumbnail(post.getThumbnail(), holder.thumbnailImageView);

        if (post.isLiked() == null) {
            AsyncImageLoader.getInstance().displayImage(holder.upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top), true);
            AsyncImageLoader.getInstance().displayImage(holder.downvoteImageView, String.valueOf(R.drawable.ic_action_arrow_bottom), true);
            holder.scoreTextView.setTextColor(res.getColor(R.color.text_secondary));
        }
        else if (post.isLiked()) {
            AsyncImageLoader.getInstance().displayImage(holder.upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top_selected), true);
            AsyncImageLoader.getInstance().displayImage(holder.downvoteImageView, String.valueOf(R.drawable.ic_action_arrow_bottom), true);
            holder.scoreTextView.setTextColor(res.getColor(R.color.upvote_reddit));
        }
        else {
            AsyncImageLoader.getInstance().displayImage(holder.upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top), true);
            AsyncImageLoader.getInstance().displayImage(holder.downvoteImageView, String.valueOf(R.drawable.ic_action_arrow_bottom_selected), true);
            holder.scoreTextView.setTextColor(res.getColor(R.color.downvote_reddit));
        }
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
        private ImageView upvoteImageView;
        private ImageView downvoteImageView;
        private ImageView socialShareImageView;
        private ImageView moreOptionsImageView;

        public PostViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_textview);
            infoTextView = (TextView) itemView.findViewById(R.id.info_textview);
            scoreTextView = (TextView) itemView.findViewById(R.id.score_textview);
            numCommentsTextView = (TextView) itemView.findViewById(R.id.num_comments_textview);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_imageview);
            upvoteImageView = (ImageView) itemView.findViewById(R.id.upvote_imageview);
            downvoteImageView = (ImageView) itemView.findViewById(R.id.downvote_imageview);
            socialShareImageView = (ImageView) itemView.findViewById(R.id.social_share_imageview);
            moreOptionsImageView = (ImageView) itemView.findViewById(R.id.more_options_imageview);

            socialShareImageView.setOnClickListener(this);
            moreOptionsImageView.setOnClickListener(this);
            upvoteImageView.setOnClickListener(this);
            downvoteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Post selectedPost = posts.get(getPosition());

            switch (v.getId()) {

                case R.id.social_share_imageview:
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
                                            Helpers.socialShareLink(context, Globals.API_BASE_URL + selectedPost.getPermalink());
                                            break;
                                    }
                                }
                            })
                            .create();
                    shareDialog.show();
                    break;

                case R.id.more_options_imageview:
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
                                            Helpers.viewURLInBrowser(context, Globals.API_BASE_URL + selectedPost.getPermalink());
                                            break;
                                    }
                                }
                            })
                            .create();
                    moreOptionsDialog.show();
                    break;

                case R.id.upvote_imageview: {
                    int voteDirection;
                    int newScore;

                    if (selectedPost.isLiked() == null) {
                        voteDirection = 1;

                        selectedPost.setLiked(true);
                        AsyncImageLoader.getInstance().displayImage(upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top_selected), true);

                        newScore = selectedPost.getScore() + 1;
                        updateScoreTextView(newScore, R.color.upvote_reddit);
                    }
                    else if (selectedPost.isLiked()) {
                        voteDirection = 0;

                        selectedPost.setLiked(null);
                        AsyncImageLoader.getInstance().displayImage(upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top), true);

                        newScore = selectedPost.getScore() - 1;
                        updateScoreTextView(newScore, R.color.text_secondary);
                    }
                    else {
                        voteDirection = 1;

                        selectedPost.setLiked(true);
                        AsyncImageLoader.getInstance().displayImage(upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top_selected), true);
                        AsyncImageLoader.getInstance().displayImage(downvoteImageView, String.valueOf(R.drawable.ic_action_arrow_bottom), true);

                        newScore = selectedPost.getScore() + 2;
                        updateScoreTextView(newScore, R.color.upvote_reddit);
                    }

                    selectedPost.setScore(newScore);
                    new VoteTask(selectedPost.getFullName(), voteDirection).execute();
                    break;
                }

                case R.id.downvote_imageview: {
                    int voteDirection;
                    int newScore;

                    if (selectedPost.isLiked() == null) {
                        voteDirection = -1;

                        selectedPost.setLiked(false);
                        AsyncImageLoader.getInstance().displayImage(downvoteImageView, String.valueOf(R.drawable.ic_action_arrow_bottom_selected), true);

                        newScore = selectedPost.getScore() - 1;
                        updateScoreTextView(newScore, R.color.downvote_reddit);
                    }
                    else if (selectedPost.isLiked()) {
                        voteDirection = -1;

                        selectedPost.setLiked(false);
                        AsyncImageLoader.getInstance().displayImage(upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top), true);
                        AsyncImageLoader.getInstance().displayImage(downvoteImageView, String.valueOf(R.drawable.ic_action_arrow_bottom_selected), true);

                        newScore = selectedPost.getScore() - 2;
                        updateScoreTextView(newScore, R.color.downvote_reddit);
                    }
                    else {
                        voteDirection = 0;

                        selectedPost.setLiked(null);
                        AsyncImageLoader.getInstance().displayImage(upvoteImageView, String.valueOf(R.drawable.ic_action_arrow_top), true);
                        AsyncImageLoader.getInstance().displayImage(downvoteImageView, String.valueOf(R.drawable.ic_action_arrow_bottom), true);

                        newScore = selectedPost.getScore() + 1;
                        updateScoreTextView(newScore, R.color.text_secondary);
                    }

                    selectedPost.setScore(newScore);
                    new VoteTask(selectedPost.getFullName(), voteDirection).execute();
                    break;
                }
            }
        }

        private void updateScoreTextView(int score, int colorRes) {
            scoreTextView.setText(score == 1 ? score + " point" : score + " points");
            scoreTextView.setTextColor(res.getColor(colorRes));
        }
    }
}