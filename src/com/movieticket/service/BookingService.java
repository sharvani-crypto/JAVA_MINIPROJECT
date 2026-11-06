package com.movieticket.service;

import com.movieticket.exception.SeatAlreadyBookedException;
import com.movieticket.model.*;
import com.movieticket.util.FileStorage;
import java.util.*;

public class BookingService {
    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private final int holdSeconds;

    public BookingService(int holdSeconds) { this.holdSeconds = holdSeconds; }

    public synchronized void holdSeat(Show show, String seatId, String token) throws SeatAlreadyBookedException {
        Seat seat = show.getSeat(seatId);
        if (seat == null || !seat.hold(token)) {
            throw new SeatAlreadyBookedException("Seat " + seatId + " is not available. Please choose another seat.");
        }
        Thread timer = new Thread(new SeatHoldTimer(this, show, seatId, token, holdSeconds),
                "SeatHoldTimer-" + show.getShowId() + "-" + seatId);
        timer.setDaemon(true);
        timer.start();
    }

    public synchronized void releaseSeat(Show show, String seatId, String token) {
        Seat seat = show.getSeat(seatId);
        if (seat != null) seat.release(token);
    }

    /** Synchronized so two users cannot confirm the same seat simultaneously. */
    public synchronized Booking confirmBooking(User user, Show show, List<String> seatIds, String token)
            throws SeatAlreadyBookedException {
        if (seatIds == null || seatIds.isEmpty()) throw new IllegalArgumentException("Select at least one seat.");
        double total = 0;
        synchronized (show) {
            for (String id : seatIds) {
                Seat seat = show.getSeat(id);
                if (seat == null || seat.getStatus() != SeatStatus.HELD || !token.equals(seat.getHoldToken())) {
                    throw new SeatAlreadyBookedException("Your hold for seat " + id + " has expired. Select it again.");
                }
                total += seat.getPrice();
            }
            for (String id : seatIds) {
                if (!show.getSeat(id).book(token))
                    throw new SeatAlreadyBookedException("Could not book seat " + id + ". Please retry.");
            }
        }
        Booking booking = new Booking(user, show, seatIds, total);
        bookings.put(booking.getBookingId(), booking);
        FileStorage.appendBooking(booking);
        return booking;
    }

    public synchronized Collection<Booking> getBookings() { return List.copyOf(bookings.values()); }
    public synchronized double getCollectionForShow(String showId) {
        return bookings.values().stream().filter(b -> b.getShow().getShowId().equals(showId))
                .mapToDouble(Booking::getTotalAmount).sum();
    }
    public synchronized void writeShowWiseReport(Collection<Show> shows) {
        FileStorage.writeShowReport(shows, bookings.values());
    }
}
