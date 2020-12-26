package com.android.socialmedia;

public class followList {
    String followingUsername, followerUsername, likerUsername;

    public followList(String followingUsername, String followerUsername, String likerUsername) {
        this.followingUsername = followingUsername;
        this.followerUsername = followerUsername;
        this.likerUsername = likerUsername;
    }

    public followList() {
    }

    public String getLikerUsername() {
        return likerUsername;
    }

    public void setLikerUsername(String likerUsername) {
        this.likerUsername = likerUsername;
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
