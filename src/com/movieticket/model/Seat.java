package com.movieticket.model;

public abstract class Seat {
    private final String seatId;
    private final SeatType type;
    private SeatStatus status = SeatStatus.AVAILABLE;
    private String holdToken;

    protected Seat(String seatId, SeatType type) {
        this.seatId = seatId;
        this.type = type;
    }

    public abstract double getPrice();
    public String getSeatId() { return seatId; }
    public SeatType getType() { return type; }
    public synchronized SeatStatus getStatus() { return status; }
    public synchronized String getHoldToken() { return holdToken; }
    public synchronized boolean hold(String token) {
        if (status != SeatStatus.AVAILABLE) return false;
        status = SeatStatus.HELD;
        holdToken = token;
        return true;
    }
    public synchronized boolean book(String token) {
        if (status != SeatStatus.HELD || !token.equals(holdToken)) return false;
        status = SeatStatus.BOOKED;
        holdToken = null;
        return true;
    }
    public synchronized void release(String token) {
        if (status == SeatStatus.HELD && token.equals(holdToken)) {
            status = SeatStatus.AVAILABLE;
            holdToken = null;
        }
    }
}
