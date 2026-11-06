package com.movieticket.model;

public class Theatre {
    private final String name;
    private final String city;
    public Theatre(String name, String city) { this.name = name; this.city = city; }
    public String getName() { return name; }
    public String getCity() { return city; }
    @Override public String toString() { return name + " - " + city; }
}
