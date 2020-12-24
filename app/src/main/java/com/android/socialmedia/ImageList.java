package com.android.socialmedia;

public class ImageList {
    String Image,caption,data;

    public ImageList() {
    }

    public ImageList(String image, String caption, String data) {
        Image = image;
        this.caption = caption;
        this.data = data;
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

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
