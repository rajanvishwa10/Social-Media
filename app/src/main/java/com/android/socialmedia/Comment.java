package com.android.socialmedia;

public class Comment {
    String comment, commenter;

    public Comment() {
    }

    public Comment(String comment, String commenter) {
        this.comment = comment;
        this.commenter = commenter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }
}
