package com.movieticket.model;

public class Movie {
    private final String title;
    private final String language;
    private final int durationMinutes;
    public Movie(String title, String language, int durationMinutes) {
        this.title = title; this.language = language; this.durationMinutes = durationMinutes;
    }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public int getDurationMinutes() { return durationMinutes; }
    @Override public String toString() { return title + " (" + language + ", " + durationMinutes + " min)"; }
}
