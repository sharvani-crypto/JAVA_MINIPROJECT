package com.movieticket.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Booking {
    private final String bookingId = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    private final User user;
    private final Show show;
    private final List<String> seatIds;
    private final double totalAmount;
    private final LocalDateTime bookedAt = LocalDateTime.now();
    public Booking(User user, Show show, List<String> seatIds, double totalAmount) {
        this.user = user; this.show = show; this.seatIds = List.copyOf(seatIds); this.totalAmount = totalAmount;
    }
    public String getBookingId() { return bookingId; }
    public User getUser() { return user; }
    public Show getShow() { return show; }
    public List<String> getSeatIds() { return seatIds; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getBookedAt() { return bookedAt; }
}
