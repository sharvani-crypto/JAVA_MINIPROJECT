package com.movieticket.model;

public class PremiumSeat extends Seat {
    public PremiumSeat(String id) { super(id, SeatType.PREMIUM); }
    @Override public double getPrice() { return 220.0; }
}
