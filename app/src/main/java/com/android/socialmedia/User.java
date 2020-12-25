package com.android.socialmedia;

public class User {
    String Username, profileImage, FullName;

    public User() {
    }

    public User(String username, String profileImage, String FullName) {
        Username = username;
        this.profileImage = profileImage;
        this.FullName = FullName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
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
