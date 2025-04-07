package com.example.negrskursack;

public class Music {
    private int id;
    private String title;
    private String artist;
    private int mp3ResourceId;
    private int imageResource;
    private boolean isFavorite;

    public Music(int id, String title, String artist, int mp3ResourceId, int imageResource, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.mp3ResourceId = mp3ResourceId;
        this.imageResource = imageResource;
        this.isFavorite = isFavorite;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getMp3ResourceId() {
        return mp3ResourceId;
    }

    public int getImageResource() {
        return imageResource;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}