package com.android.redditreader.fragments;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.redditreader.R;
import com.android.redditreader.activities.MainActivity;
import com.android.redditreader.utils.Globals;

import java.util.ArrayList;


public class NavigationDrawerFragment extends Fragment {

    private RecyclerView subredditsRecyclerView;
    private MainActivity mainActivity;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        findViews(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        // set up subreddits recycler view
        subredditsRecyclerView.setAdapter(new NavigationDrawerSubredditsAdapter(new ArrayList<String>()));
        subredditsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void findViews(View view) {
        subredditsRecyclerView = (RecyclerView) view.findViewById(R.id.subreddits_recycler_view);
    }


    private class NavigationDrawerSubredditsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final String TAG = NavigationDrawerSubredditsAdapter.class.getSimpleName();

        private final int TYPE_HEADAER = 0;
        private final int TYPE_ITEM = 1;

        private ArrayList<String> subreddits;

        private int currentPos = 1;
        private int oldPos = 1;

        public NavigationDrawerSubredditsAdapter(ArrayList<String> subreddits) {
            this.subreddits = subreddits;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = null;

            if (viewType == TYPE_HEADAER) {
                itemView = LayoutInflater.from(mainActivity).inflate(R.layout.header_navigation_drawer_subreddits, parent, false);
                return new HeaderViewHolder(itemView);
            }
            else {
                itemView = LayoutInflater.from(mainActivity).inflate(R.layout.row_navigation_drawer_subreddits, parent, false);
                return new SubredditViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SubredditViewHolder) {
                SubredditViewHolder subredditViewHolder = (SubredditViewHolder) holder;
                subredditViewHolder.subreddit.setText(subreddits.get(position - 1));

                Resources res = mainActivity.getResources();
                if (position == currentPos) {
                    subredditViewHolder.subreddit.setTextColor(res.getColor(R.color.accent));
                    subredditViewHolder.subreddit.setBackgroundColor(res.getColor(R.color.navigation_drawer_selected_item_background));
                }
                else {
                    subredditViewHolder.subreddit.setTextColor(res.getColor(R.color.text_primary));
                    subredditViewHolder.subreddit.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }

        @Override
        public int getItemCount() {
            return subreddits.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADAER;
            }
            else {
                return TYPE_ITEM;
            }
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {

            private View addAccountContainer;
            private View accountInfoContainer;
            private View viewUserProfileContainer;
            private View manageSubredditsContainer;
            private View viewSettingsContainer;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                accountInfoContainer = itemView.findViewById(R.id.account_info_container);
                addAccountContainer = itemView.findViewById(R.id.add_account_container);
                viewUserProfileContainer = itemView.findViewById(R.id.view_user_profile_container);
                manageSubredditsContainer = itemView.findViewById(R.id.manage_subreddits_container);
                viewSettingsContainer = itemView.findViewById(R.id.view_setttings_container);
            }
        }

        private class SubredditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView subreddit;

            public SubredditViewHolder(View itemView) {
                super(itemView);

                subreddit = (TextView) itemView.findViewById(R.id.subreddit);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // highlight selected item and un-highlight previously selected item
                oldPos = currentPos;
                currentPos = this.getPosition();

                notifyItemChanged(oldPos);
                notifyItemChanged(currentPos);

                //close navigation drawer and refresh posts for the selected subreddit
                mainActivity.drawerLayout.closeDrawer(mainActivity.navigationDrawerContainer);
                Globals.CURRENT_SUBREDDIT = subreddit.getText().toString();
                mainActivity.postsListFragment.refreshPosts();
                mainActivity.updateActionBarText();
            }
        }

    }

}
