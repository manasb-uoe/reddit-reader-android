package com.android.redditreader.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.redditreader.R;

public class PostsListFragment extends Fragment {

    private RecyclerView postsRecyclerView;
    private ProgressBar postsProgressBar;

    public PostsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);
        findViews(view);

        return view;
    }

    private void findViews(View view) {
        postsRecyclerView = (RecyclerView) view.findViewById(R.id.posts_recycler_view);
        postsProgressBar = (ProgressBar) view.findViewById(R.id.posts_progress_bar);
    }

}
