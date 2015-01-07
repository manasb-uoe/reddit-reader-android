package com.android.redditreader.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.redditreader.R;
import com.android.redditreader.models.Subreddit;
import com.android.redditreader.utils.Globals;

import java.util.ArrayList;

public class FavouriteSubredditsAdapter extends RecyclerView.Adapter<FavouriteSubredditsAdapter.SubredditViewHolder> {

    private static final String TAG = FavouriteSubredditsAdapter.class.getSimpleName();

    private Context context;
    public ArrayList<Subreddit> subreddits;

    private int accentColor;
    private int textPrimaryColor;
    private Typeface normalTypeFace;
    private Typeface boldTypeFace;

    public FavouriteSubredditsAdapter(Context context, ArrayList<Subreddit> subreddits) {
        this.context = context;
        this.subreddits = subreddits;

        Resources res = context.getResources();
        accentColor = res.getColor(R.color.accent);
        textPrimaryColor = res.getColor(R.color.text_primary);
        normalTypeFace = Typeface.create("sans-serif", Typeface.NORMAL);
        boldTypeFace = Typeface.create("sans-serif", Typeface.BOLD);
    }

    @Override
    public SubredditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_favourite_subreddits, parent, false);
        return new SubredditViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubredditViewHolder holder, int position) {
        Subreddit subreddit = subreddits.get(position);

        holder.subredditTextView.setText(subreddit.getName());
        if (!subreddit.getName().equals(Globals.DEFAULT_SUBREDDIT)) {
            holder.numSubscribersTextView.setVisibility(View.VISIBLE);
            holder.numSubscribersTextView.setText(subreddit.getNumOfSubscribers() + " subscribers");
        }
        else {
            holder.numSubscribersTextView.setVisibility(View.GONE);
        }

        if (subreddit.isFavourite()) {
            holder.setChecked(true);
        }
        else {
            holder.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return subreddits.size();
    }

    public void updateDataSetAndNotifyChanges(ArrayList<Subreddit> subreddits) {
        this.subreddits.clear();
        this.subreddits.addAll(subreddits);
        this.subreddits.trimToSize();
        notifyDataSetChanged();
    }

    public ArrayList<Subreddit> getFavouriteSubreddits() {
        ArrayList<Subreddit> favouriteSubreddits = new ArrayList<>();

        for (Subreddit subreddit : subreddits) {
            if (subreddit.isFavourite()) {
                favouriteSubreddits.add(subreddit);
            }
        }

        return favouriteSubreddits;
    }

    public class SubredditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView subredditTextView;
        private TextView numSubscribersTextView;
        private CheckBox favouriteCheckBox;

        public SubredditViewHolder(View itemView) {
            super(itemView);

            subredditTextView = (TextView) itemView.findViewById(R.id.subreddit_textview);
            numSubscribersTextView = (TextView) itemView.findViewById(R.id.num_subscribers_textview);
            favouriteCheckBox = (CheckBox) itemView.findViewById(R.id.favourite_checkbox);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Subreddit selectedSubreddit = subreddits.get(getPosition());

            if (!selectedSubreddit.getName().equals(Globals.DEFAULT_SUBREDDIT)) {
                if (selectedSubreddit.isFavourite()) {
                    selectedSubreddit.setFavourite(false);
                    setChecked(false);
                }
                else {
                    selectedSubreddit.setFavourite(true);
                    setChecked(true);
                }
            }
            else {
                Toast.makeText(context, R.string.error_cannot_remove_front_page_from_favourites, Toast.LENGTH_SHORT)
                        .show();
            }
        }
        private void setChecked(boolean checked) {
            if (checked) {
                favouriteCheckBox.setChecked(true);
                subredditTextView.setTypeface(boldTypeFace);
                subredditTextView.setTextColor(accentColor);
            }
            else {
                favouriteCheckBox.setChecked(false);
                subredditTextView.setTypeface(normalTypeFace);
                subredditTextView.setTextColor(textPrimaryColor);
            }
        }
    }
}
