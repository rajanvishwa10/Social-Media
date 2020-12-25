package com.android.socialmedia;

public class followList {
    String followingUsername, followerUsername;

    public followList(String followingUsername, String followerUsername) {
        this.followingUsername = followingUsername;
        this.followerUsername = followerUsername;
    }

    public followList() {
    }

    public String getFollowingUsername() {
        return followingUsername;
    }

    public String getFollowerUsername() {
        return followerUsername;
    }

    public void setFollowerUsername(String followUsername) {
        this.followerUsername = followUsername;
    }

    public void setFollowingUsername(String followingUsername) {
        this.followingUsername = followingUsername;
    }
}
