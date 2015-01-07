package com.android.redditreader.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;

/**
 * Created by Manas on 02-01-2015.
 */
public class Subreddit implements Parcelable {

    private String name;
    private String numOfSubscribers;
    private boolean isSubscribed;
    private String description;
    private boolean isFavourite;

    public Subreddit() {
        //empty constructor
    }

    // Parcel constructor
    public Subreddit(Parcel source) {
        this.name = source.readString();
        this.numOfSubscribers = source.readString();
        this.isSubscribed = source.readInt() == 1;
        this.description = source.readString();
        this.isFavourite = source.readInt() == 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumOfSubscribers() {
        return numOfSubscribers;
    }

    public void setNumOfSubscribers(String numOfSubscribers) {
        this.numOfSubscribers = NumberFormat.getInstance().format(Long.valueOf(numOfSubscribers));
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean isSubscribed) {
        this.isSubscribed = isSubscribed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    // --------Parcelling--------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(numOfSubscribers);
        dest.writeInt(isSubscribed ? 1 : 0);
        dest.writeString(description);
        dest.writeInt(isFavourite ? 1 : 0);
    }

    public static final Creator<Subreddit> CREATOR = new Creator<Subreddit>() {
        @Override
        public Subreddit createFromParcel(Parcel source) {
            return new Subreddit(source);
        }

        @Override
        public Subreddit[] newArray(int size) {
            return new Subreddit[size];
        }
    };
}
