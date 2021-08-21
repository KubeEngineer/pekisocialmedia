package com.example.chatpeki.Model;

public class Yorum {
    private String gonderen_id,yorum;

    public Yorum() {
    }

    public Yorum(String gonderen_id, String yorum) {
        this.gonderen_id = gonderen_id;
        this.yorum = yorum;
    }

    public String getGonderen_id() {
        return gonderen_id;
    }

    public void setGonderen_id(String gonderen_id) {
        this.gonderen_id = gonderen_id;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }
}
