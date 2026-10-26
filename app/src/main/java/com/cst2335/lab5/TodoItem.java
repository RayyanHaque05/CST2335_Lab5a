package com.cst2335.lab5;

public class TodoItem {
    private long id;
    private String text;
    private boolean isUrgent;

    public TodoItem(String text, boolean isUrgent, long id) {
        this.text = text;
        this.isUrgent = isUrgent;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }
}
