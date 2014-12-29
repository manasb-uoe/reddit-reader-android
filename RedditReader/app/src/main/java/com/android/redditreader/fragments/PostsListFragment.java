package com.android.redditreader.fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.redditreader.R;
import com.android.redditreader.adapters.PostsAdapter;
import com.android.redditreader.background_tasks.GetPostsTask;
import com.android.redditreader.models.Post;
import com.android.redditreader.utils.Helpers;

import java.util.ArrayList;

public class PostsListFragment extends Fragment {

    private RecyclerView postsRecyclerView;
    private ProgressBar postsProgressBar;
    private PostsAdapter postsAdapter;

    public PostsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);
        findViews(view);

        // set up posts recycler view
        postsAdapter = new PostsAdapter(getActivity(), new ArrayList<Post>());
        postsRecyclerView.setAdapter(postsAdapter);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshPosts();

        return view;
    }

    private void findViews(View view) {
        postsRecyclerView = (RecyclerView) view.findViewById(R.id.posts_recycler_view);
        postsProgressBar = (ProgressBar) view.findViewById(R.id.posts_progress_bar);
    }

    public void refreshPosts() {
        new GetPostsTask(Helpers.getCurrentSubredditLink(), postsAdapter, postsRecyclerView, postsProgressBar).execute();
    }

}
