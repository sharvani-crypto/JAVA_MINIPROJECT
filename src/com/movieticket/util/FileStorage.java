package com.movieticket.util;

import com.movieticket.model.Booking;
import com.movieticket.model.Show;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collection;

public final class FileStorage {
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path BOOKINGS_FILE = DATA_DIR.resolve("bookings.csv");
    private static final Path REPORT_FILE = DATA_DIR.resolve("show_wise_collection_report.txt");
    private FileStorage() { }

    public static synchronized void appendBooking(Booking b) {
        try {
            Files.createDirectories(DATA_DIR);
            if (!Files.exists(BOOKINGS_FILE)) {
                Files.writeString(BOOKINGS_FILE, "Booking ID,Customer,Email,Movie,Theatre,Show ID,Seats,Amount,Booked At\n",
                        StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }
            String line = String.join(",", csv(b.getBookingId()), csv(b.getUser().getName()),
                    csv(b.getUser().getEmail()), csv(b.getShow().getMovie().getTitle()),
                    csv(b.getShow().getTheatre().getName()), csv(b.getShow().getShowId()),
                    csv(String.join(" ", b.getSeatIds())), String.format(java.util.Locale.US, "%.2f", b.getTotalAmount()),
                    csv(b.getBookedAt().toString())) + "\n";
            Files.writeString(BOOKINGS_FILE, line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) { throw new IllegalStateException("Unable to save booking: " + e.getMessage(), e); }
    }

    public static synchronized void writeShowReport(Collection<Show> shows, Collection<Booking> bookings) {
        try {
            Files.createDirectories(DATA_DIR);
            StringBuilder out = new StringBuilder("SHOW-WISE COLLECTION REPORT\n================================\n");
            for (Show show : shows) {
                double total = bookings.stream().filter(b -> b.getShow().getShowId().equals(show.getShowId()))
                        .mapToDouble(Booking::getTotalAmount).sum();
                long count = bookings.stream().filter(b -> b.getShow().getShowId().equals(show.getShowId())).count();
                out.append("Show ID: ").append(show.getShowId()).append(" | Movie: ")
                   .append(show.getMovie().getTitle()).append(" | Theatre: ").append(show.getTheatre().getName())
                   .append(" | Bookings: ").append(count).append(" | Collection: Rs. ")
                   .append(String.format(java.util.Locale.US, "%.2f", total)).append('\n');
            }
            Files.writeString(REPORT_FILE, out.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) { throw new IllegalStateException("Unable to write report: " + e.getMessage(), e); }
    }
    private static String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}
