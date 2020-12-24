package com.android.socialmedia;

public class User {
    String Username, profileImage;

    public User() {
    }

    public User(String username, String profileImage) {
        Username = username;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
