# Movie Ticket Booking System (BookMyShow Style)

A Java Swing mini-project that simulates movie-show selection, seat selection, temporary seat holds, booking confirmation, and show-wise collection reporting.

## Requirements covered

- **Java JDK + Swing:** desktop GUI with event handling.
- **Core classes:** `Movie`, `Theatre`, `Screen`, `Show`, `Seat`, `Booking`, and `User`.
- **Seat categories:** `RegularSeat`, `PremiumSeat`, and `ReclinerSeat` extend the abstract `Seat` class. Their overridden `getPrice()` methods demonstrate inheritance and runtime polymorphism.
- **Seat states:** `SeatStatus` enum: `AVAILABLE`, `HELD`, `BOOKED`.
- **Temporary hold:** each selected seat is held for 30 seconds. `SeatHoldTimer` implements `Runnable`; its thread releases the seat if it is still held by the same booking token.
- **Thread safety:** seat state methods are synchronized, and booking confirmation is synchronized and validates all selected seats before marking them booked.
- **Custom exception:** `SeatAlreadyBookedException` handles unavailable/expired seats.
- **Collections:** `HashMap` stores the seat buttons; `LinkedHashMap` stores seats and bookings.
- **File handling:** confirmed bookings append to `data/bookings.csv`; the report button writes `data/show_wise_collection_report.txt`.

> Payment is simulated by clicking **Confirm Booking / Simulate Payment**. This is an academic demonstration and does not connect to a real payment gateway or online database.

## Run in VS Code / terminal

1. Install JDK 17 or later and the Java Extension Pack in VS Code.
2. Open the `MovieTicketBookingSystem` folder in VS Code.
3. Open the terminal in that folder.
4. Compile all source files:

   **Windows PowerShell:**
   ```powershell
   javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
   ```
   **macOS / Linux:**
   ```bash
   mkdir -p out
   javac -d out $(find src -name "*.java")
   ```
5. Run:
   ```bash
   java -cp out com.movieticket.app.Main
   ```

Bookings and reports are created in a `data` folder in the working directory when you use those features.

## How to use

1. Enter a customer name and email.
2. Select a movie show from the drop-down list.
3. Click one or more green seats. Each seat becomes held (yellow) for 30 seconds.
4. Click **Confirm Booking / Simulate Payment** before the hold expires.
5. Booked seats turn red and cannot be selected again.
6. Click **Generate Show-wise Collection Report** to create the report text file.
7. **Cancel My Holds** releases the seats you currently hold.

Seat pricing in this demonstration:

| Seat category | Rows | Price |
|---|---:|---:|
| Recliner | A-B | Rs. 350 |
| Premium | C-D | Rs. 220 |
| Regular | E | Rs. 150 |

## Package structure

```text
src/com/movieticket/
├── app/        Main.java
├── exception/  SeatAlreadyBookedException.java
├── model/      Movie, Theatre, Screen, Show, Seat, seat subclasses,
│               Booking, User, SeatType, SeatStatus
├── service/    BookingService.java, SeatHoldTimer.java
├── ui/         BookingFrame.java
└── util/       FileStorage.java
```

## UML class diagram (PlantUML)

Paste this into a PlantUML renderer to export the UML diagram as an image:

```plantuml
@startuml
package "model" {
  class Movie
  class Theatre
  class Screen
  class Show
  abstract class Seat {
    - seatId: String
    - type: SeatType
    - status: SeatStatus
    + getPrice(): double
    + hold(token): boolean
    + book(token): boolean
    + release(token): void
  }
  class RegularSeat
  class PremiumSeat
  class ReclinerSeat
  class Booking
  class User
  enum SeatType
  enum SeatStatus
  Seat <|-- RegularSeat
  Seat <|-- PremiumSeat
  Seat <|-- ReclinerSeat
  Show *-- Seat
  Show --> Movie
  Show --> Theatre
  Show --> Screen
  Booking --> User
  Booking --> Show
  Seat --> SeatType
  Seat --> SeatStatus
}
package "service" {
  class BookingService
  class SeatHoldTimer
  BookingService ..> SeatHoldTimer : starts thread
  BookingService ..> Booking
}
package "exception" { class SeatAlreadyBookedException }
package "util" { class FileStorage }
package "ui" { class BookingFrame }
BookingFrame --> BookingService
BookingService ..> SeatAlreadyBookedException
BookingService ..> FileStorage
SeatHoldTimer ..> BookingService : releases expired hold
@enduml
```

## Suggested division for a five-member team

1. **Member 1 – Movies, theatres and shows:** `Movie`, `Theatre`, `Screen`, `Show`; sample show data.
2. **Member 2 – Seat map:** `Seat`, seat subclasses, `SeatType`, `SeatStatus`; available/held/booked seat display.
3. **Member 3 – Seat hold timer:** `SeatHoldTimer`, hold token logic, expiry and synchronization support.
4. **Member 4 – Booking and fare:** `Booking`, `User`, `BookingService`, custom exception, total calculation and cancellation of holds.
5. **Member 5 – Reports and file storage:** `FileStorage`, CSV booking history, show-wise collection report, integration and testing.

## Notes for demonstration

- Open two application windows to demonstrate that the same `Show` object is not shared across separate processes; for a true multi-user/network demonstration, a shared server/database would be needed. Within this application, synchronized seat operations prevent concurrent booking conflicts on the shared in-memory show object.
- Data is stored locally in CSV/text files. The project does not include a database because the assignment specifies Java, Swing and file handling.
- For a production application, use a database transaction and a backend service to coordinate seats across multiple computers.
