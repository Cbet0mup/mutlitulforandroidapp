package com.pavel.multitool.noteFileSupplement;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TextTableModel implements Serializable {
    private int id;
    private String title;
    private String body;

    public TextTableModel() {
    }

    public TextTableModel(String title, String body) {
        super();
        this.title = title;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @NonNull
    @Override
    public String toString() {
        return "Note [id=" + id + ", title=" + title + ", body=" + body
                + "]";
    }
}
