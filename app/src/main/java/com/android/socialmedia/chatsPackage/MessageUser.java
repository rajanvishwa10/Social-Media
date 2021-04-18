package com.android.socialmedia.chatsPackage;

public class MessageUser implements Comparable<MessageUser>{
    String id;
    Long date;

    public MessageUser() {
    }

    public MessageUser(String id, Long date) {
        this.id = id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public int compareTo(MessageUser o) {
        if (getDate()== null || o.getDate() == null) {
            return 0;
        }
        return getDate().compareTo(o.getDate());
    }
}
