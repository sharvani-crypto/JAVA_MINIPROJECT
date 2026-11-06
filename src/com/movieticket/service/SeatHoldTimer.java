package com.movieticket.service;

import com.movieticket.model.Show;

/** Releases a held seat when its payment window expires. */
public class SeatHoldTimer implements Runnable {
    private final BookingService bookingService;
    private final Show show;
    private final String seatId;
    private final String token;
    private final int holdSeconds;

    public SeatHoldTimer(BookingService service, Show show, String seatId, String token, int holdSeconds) {
        this.bookingService = service; this.show = show; this.seatId = seatId;
        this.token = token; this.holdSeconds = holdSeconds;
    }
    @Override public void run() {
        try {
            Thread.sleep(holdSeconds * 1000L);
            bookingService.releaseSeat(show, seatId, token);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
