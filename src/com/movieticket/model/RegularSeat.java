package com.movieticket.model;

public class RegularSeat extends Seat {
    public RegularSeat(String id) { super(id, SeatType.REGULAR); }
    @Override public double getPrice() { return 150.0; }
}
