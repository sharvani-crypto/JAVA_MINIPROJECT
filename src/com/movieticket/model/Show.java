package com.movieticket.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Show {
    private final String showId;
    private final Movie movie;
    private final Theatre theatre;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final Map<String, Seat> seats = new LinkedHashMap<>();

    public Show(String showId, Movie movie, Theatre theatre, Screen screen, LocalDateTime startTime) {
        this.showId = showId; this.movie = movie; this.theatre = theatre;
        this.screen = screen; this.startTime = startTime;
        createSeats();
    }
    private void createSeats() {
        for (int r = 0; r < screen.getRows(); r++) {
            char row = (char) ('A' + r);
            for (int c = 1; c <= screen.getColumns(); c++) {
                String id = row + String.valueOf(c);
                Seat seat;
                if (r < 2) seat = new ReclinerSeat(id);
                else if (r < 4) seat = new PremiumSeat(id);
                else seat = new RegularSeat(id);
                seats.put(id, seat);
            }
        }
    }
    public String getShowId() { return showId; }
    public Movie getMovie() { return movie; }
    public Theatre getTheatre() { return theatre; }
    public Screen getScreen() { return screen; }
    public LocalDateTime getStartTime() { return startTime; }
    public Map<String, Seat> getSeats() { return Collections.unmodifiableMap(seats); }
    public Seat getSeat(String id) { return seats.get(id); }
    @Override public String toString() {
        return movie.getTitle() + " | " + theatre.getName() + " | " + startTime.toLocalDate() + " " + startTime.toLocalTime();
    }
}
