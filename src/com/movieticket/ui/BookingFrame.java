package com.movieticket.ui;

import com.movieticket.exception.SeatAlreadyBookedException;
import com.movieticket.model.*;
import com.movieticket.service.BookingService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class BookingFrame extends JFrame {
    private static final int HOLD_SECONDS = 30;
    private final BookingService bookingService = new BookingService(HOLD_SECONDS);
    private final List<Show> shows = createShows();
    private final Set<String> selectedSeats = new LinkedHashSet<>();
    private String holdToken = UUID.randomUUID().toString();
    private Show currentShow;
    private JPanel seatPanel;
    private JLabel statusLabel;
    private JLabel selectionLabel;
    private JComboBox<Show> showCombo;
    private JTextField nameField;
    private JTextField emailField;
    private final Map<String, JButton> seatButtons = new HashMap<>();

    public BookingFrame() {
        super("Movie Ticket Booking - Java Mini Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 720);
        setLocationRelativeTo(null);
        setContentPane(buildMainPanel());
        currentShow = shows.get(0);
        refreshSeats();
        new javax.swing.Timer(1000, e -> refreshSeats()).start();
    }

    private JPanel buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(15, 18, 15, 18));
        root.setBackground(new Color(245, 247, 250));

        JLabel heading = new JLabel("MOVIE TICKET BOOKING", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 25));
        heading.setForeground(new Color(27, 70, 110));
        root.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        JPanel details = new JPanel(new GridLayout(2, 4, 8, 8));
        details.setBorder(BorderFactory.createTitledBorder("Customer and Show Details"));
        nameField = new JTextField("Guest User");
        emailField = new JTextField("guest@example.com");
        showCombo = new JComboBox<>(shows.toArray(new Show[0]));
        showCombo.addActionListener(e -> changeShow());
        details.add(new JLabel("Customer name:")); details.add(nameField);
        details.add(new JLabel("Email:")); details.add(emailField);
        details.add(new JLabel("Select show:")); details.add(showCombo);
        details.add(new JLabel("Hold duration:")); details.add(new JLabel(HOLD_SECONDS + " seconds (payment window)"));
        center.add(details, BorderLayout.NORTH);

        JPanel cinema = new JPanel(new BorderLayout(8, 8));
        cinema.setBorder(BorderFactory.createTitledBorder("Select Your Seats"));
        JLabel screen = new JLabel("SCREEN THIS WAY", SwingConstants.CENTER);
        screen.setOpaque(true); screen.setBackground(new Color(55, 65, 81)); screen.setForeground(Color.WHITE);
        screen.setFont(new Font("SansSerif", Font.BOLD, 14)); screen.setBorder(new EmptyBorder(8, 8, 8, 8));
        cinema.add(screen, BorderLayout.NORTH);
        seatPanel = new JPanel(new GridLayout(5, 8, 8, 8));
        seatPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        cinema.add(seatPanel, BorderLayout.CENTER);
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 5));
        legend.add(legendItem("Available", new Color(218, 238, 220)));
        legend.add(legendItem("Selected / Held", new Color(255, 211, 105)));
        legend.add(legendItem("Booked", new Color(220, 100, 100)));
        cinema.add(legend, BorderLayout.SOUTH);
        center.add(cinema, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        selectionLabel = new JLabel("Selected seats: none | Total: Rs. 0.00");
        selectionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel = new JLabel("Choose available seats. Your seats will be held for 30 seconds.");
        JButton confirm = new JButton("Confirm Booking / Simulate Payment");
        confirm.addActionListener(e -> confirmBooking());
        JButton cancel = new JButton("Cancel My Holds");
        cancel.addActionListener(e -> clearSelection("Your seat holds were cancelled."));
        JButton report = new JButton("Generate Show-wise Collection Report");
        report.addActionListener(e -> generateReport());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        actions.setOpaque(false); actions.add(confirm); actions.add(cancel); actions.add(report);
        JPanel labels = new JPanel(new GridLayout(2, 1, 3, 3));
        labels.setOpaque(false); labels.add(selectionLabel); labels.add(statusLabel);
        bottom.add(labels, BorderLayout.NORTH); bottom.add(actions, BorderLayout.SOUTH);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private JPanel legendItem(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel swatch = new JLabel("   "); swatch.setOpaque(true); swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(Color.GRAY)); p.add(swatch); p.add(new JLabel(text));
        return p;
    }

    private void changeShow() {
        if (currentShow != null) clearSelection(null);
        currentShow = (Show) showCombo.getSelectedItem();
        refreshSeats();
    }

    private void refreshSeats() {
        if (seatPanel == null || currentShow == null) return;
        // Remove selections whose 30-second hold has expired or was otherwise released.
        selectedSeats.removeIf(id -> {
            Seat held = currentShow.getSeat(id);
            return held == null || held.getStatus() != SeatStatus.HELD || !holdToken.equals(held.getHoldToken());
        });
        seatPanel.removeAll(); seatButtons.clear();
        for (Seat seat : currentShow.getSeats().values()) {
            JButton button = new JButton(seat.getSeatId() + "\n" + seat.getType().name());
            button.setFont(new Font("SansSerif", Font.PLAIN, 10));
            button.setPreferredSize(new Dimension(88, 55));
            button.setOpaque(true); button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            button.setToolTipText(seat.getType() + " - Rs. " + String.format("%.0f", seat.getPrice()));
            updateSeatButton(button, seat);
            button.setEnabled(seat.getStatus() != SeatStatus.BOOKED &&
                    (seat.getStatus() == SeatStatus.AVAILABLE || holdToken.equals(seat.getHoldToken())));
            button.addActionListener(e -> toggleSeat(seat));
            seatButtons.put(seat.getSeatId(), button); seatPanel.add(button);
        }
        seatPanel.revalidate(); seatPanel.repaint(); updateSelectionLabel();
    }

    private void updateSeatButton(JButton button, Seat seat) {
        if (seat.getStatus() == SeatStatus.BOOKED) button.setBackground(new Color(220, 100, 100));
        else if (seat.getStatus() == SeatStatus.HELD) button.setBackground(new Color(255, 211, 105));
        else button.setBackground(new Color(218, 238, 220));
    }

    private void toggleSeat(Seat seat) {
        try {
            if (selectedSeats.contains(seat.getSeatId())) {
                bookingService.releaseSeat(currentShow, seat.getSeatId(), holdToken);
                selectedSeats.remove(seat.getSeatId());
            } else {
                bookingService.holdSeat(currentShow, seat.getSeatId(), holdToken);
                selectedSeats.add(seat.getSeatId());
                statusLabel.setText("Seat " + seat.getSeatId() + " held. Complete payment within " + HOLD_SECONDS + " seconds.");
            }
            refreshSeats();
        } catch (SeatAlreadyBookedException ex) {
            statusLabel.setText(ex.getMessage()); refreshSeats();
        }
    }

    private void confirmBooking() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Enter a valid customer name and email.", "Customer details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            User user = new User("U" + System.currentTimeMillis(), name, email);
            var booking = bookingService.confirmBooking(user, currentShow, new ArrayList<>(selectedSeats), holdToken);
            JOptionPane.showMessageDialog(this,
                    "BOOKING CONFIRMED!\nBooking ID: " + booking.getBookingId() +
                    "\nMovie: " + currentShow.getMovie().getTitle() + "\nSeats: " + booking.getSeatIds() +
                    "\nTotal: Rs. " + String.format("%.2f", booking.getTotalAmount()) +
                    "\n\nBooking saved in data/bookings.csv", "Payment successful", JOptionPane.INFORMATION_MESSAGE);
            selectedSeats.clear(); holdToken = UUID.randomUUID().toString();
            statusLabel.setText("Booking successful. Select seats for another booking."); refreshSeats();
        } catch (SeatAlreadyBookedException | IllegalArgumentException ex) {
            statusLabel.setText(ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking not completed", JOptionPane.WARNING_MESSAGE);
            refreshSeats();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSelection(String message) {
        if (currentShow != null) for (String id : selectedSeats) bookingService.releaseSeat(currentShow, id, holdToken);
        selectedSeats.clear(); holdToken = UUID.randomUUID().toString();
        if (message != null && statusLabel != null) statusLabel.setText(message);
        if (seatPanel != null) refreshSeats();
    }

    private void updateSelectionLabel() {
        double total = 0;
        for (String id : selectedSeats) {
            Seat seat = currentShow.getSeat(id);
            if (seat != null && seat.getStatus() == SeatStatus.HELD && holdToken.equals(seat.getHoldToken())) total += seat.getPrice();
        }
        selectionLabel.setText("Selected seats: " + (selectedSeats.isEmpty() ? "none" : selectedSeats) +
                " | Total: Rs. " + String.format("%.2f", total));
    }

    private void generateReport() {
        try {
            bookingService.writeShowWiseReport(shows);
            JOptionPane.showMessageDialog(this, "Report generated at data/show_wise_collection_report.txt",
                    "Report generated", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Report error", JOptionPane.ERROR_MESSAGE); }
    }

    private static List<Show> createShows() {
        Theatre theatre = new Theatre("MNR Cinemas", "Hyderabad");
        Screen screen = new Screen("Screen 1", 5, 8);
        LocalDate today = LocalDate.now();
        return List.of(
                new Show("S101", new Movie("The Java Adventure", "English", 140), theatre, screen, LocalDateTime.of(today, java.time.LocalTime.of(10, 30))),
                new Show("S102", new Movie("The Java Adventure", "English", 140), theatre, screen, LocalDateTime.of(today, java.time.LocalTime.of(14, 30))),
                new Show("S103", new Movie("Code Warriors", "Telugu",  F120), theatre, screen, LocalDateTime.of(today, java.time.LocalTime.of(18, 30)))
        );
    }
    private static final int F120 = 120;
}
