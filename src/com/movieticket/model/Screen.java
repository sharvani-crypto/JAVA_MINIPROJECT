package com.movieticket.model;

public class Screen {
    private final String screenName;
    private final int rows;
    private final int columns;
    public Screen(String screenName, int rows, int columns) {
        this.screenName = screenName; this.rows = rows; this.columns = columns;
    }
    public String getScreenName() { return screenName; }
    public int getRows() { return rows; }
    public int getColumns() { return columns; }
}
