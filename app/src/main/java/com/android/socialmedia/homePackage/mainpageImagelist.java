package com.android.socialmedia.homePackage;

public class mainpageImagelist {
    String Image, caption, data, username;
    public mainpageImagelist() {
    }

    public mainpageImagelist(String image, String caption, String data, String username) {
        Image = image;
        this.caption = caption;
        this.data = data;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
