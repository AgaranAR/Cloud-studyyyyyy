package com.example.clo;

public class Subject {
    private String name;
    private boolean favorite;

    public Subject(String name, boolean favorite) {
        this.name = name;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}