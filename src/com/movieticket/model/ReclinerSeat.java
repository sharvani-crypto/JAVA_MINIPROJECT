package com.movieticket.model;

public class ReclinerSeat extends Seat {
    public ReclinerSeat(String id) { super(id, SeatType.RECLINER); }
    @Override public double getPrice() { return 350.0; }
}
