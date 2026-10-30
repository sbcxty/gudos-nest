package com.gudosnest.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * One row from the Bookings table, plus the joined display names
 * (renter / property / unit) that the SQL query pulls in for the UI.
 */
public class Booking {
    private static final DateTimeFormatter DB_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final int bookingId;
    private final int renterId;
    private final int unitId;
    private final String createdAt;   // display string
    private final String checkIn;     // display string
    private final String checkOut;    // display string
    private final int numberOfRenters;
    private final String specialRequests; // may be null
    private final String bookingStatus;   // pending|approved|rejected|cancelled|completed
    private final BigDecimal totalAmount;
    private final String cancelledAt; // display string, may be null

    // Joined display names (filled by list/detail queries).
    private final String renterName;
    private final String renterEmail;
    private final String propertyName;
    private final String unitName;

    public Booking(int bookingId, int renterId, int unitId,
                   String createdAt, String checkIn, String checkOut,
                   int numberOfRenters, String specialRequests,
                   String bookingStatus, BigDecimal totalAmount,
                   String cancelledAt, String renterName, String renterEmail,
                   String propertyName, String unitName) {
        this.bookingId = bookingId;
        this.renterId = renterId;
        this.unitId = unitId;
        this.createdAt = createdAt;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfRenters = numberOfRenters;
        this.specialRequests = specialRequests;
        this.bookingStatus = bookingStatus;
        this.totalAmount = totalAmount;
        this.cancelledAt = cancelledAt;
        this.renterName = renterName;
        this.renterEmail = renterEmail;
        this.propertyName = propertyName;
        this.unitName = unitName;
    }

    public int getBookingId()        { return bookingId; }
    public int getRenterId()         { return renterId; }
    public int getUnitId()           { return unitId; }
    public String getCreatedAt()     { return createdAt; }
    public String getCheckIn()       { return checkIn; }
    public String getCheckOut()      { return checkOut; }
    public int getNumberOfRenters()  { return numberOfRenters; }
    public String getSpecialRequests(){ return specialRequests; }
    public String getBookingStatus() { return bookingStatus; }
    public BigDecimal getTotalAmount(){ return totalAmount; }
    public String getCancelledAt()   { return cancelledAt; }
    public String getRenterName()    { return renterName; }
    public String getRenterEmail()   { return renterEmail; }
    public String getPropertyName()  { return propertyName; }
    public String getUnitName()      { return unitName; }

    // ---- BUILDER --------------------------------------------------------
    /**
     * BUILDER: a booking has a lot of fields, but only a few are truly
     * required to start a reservation. The required ones live in the
     * constructor; the optional ones (special requests, total) are set by
     * chained methods. Dates are given as LocalDate and stored in the
     * "yyyy-MM-dd HH:mm:ss" shape the Bookings table expects.
     *
     *   Booking b = new Booking.Builder(renterId, unitId, in, out, guests)
     *                  .specialRequests("Late check-in")
     *                  .totalAmount(total)
     *                  .build();
     */
    public static final class Builder {
        private final int renterId;
        private final int unitId;
        private final LocalDate checkIn;
        private final LocalDate checkOut;
        private final int numberOfRenters;
        private String specialRequests;
        private BigDecimal totalAmount;

        /** Required fields: who books, which unit, when, and how many guests. */
        public Builder(int renterId, int unitId,
                       LocalDate checkIn, LocalDate checkOut,
                       int numberOfRenters) {
            this.renterId = renterId;
            this.unitId = unitId;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.numberOfRenters = numberOfRenters;
        }

        public Builder specialRequests(String requests) {
            this.specialRequests = requests;
            return this;
        }

        public Builder totalAmount(BigDecimal amount) {
            this.totalAmount = amount;
            return this;
        }

        /** Validates the required fields and produces an immutable Booking. */
        public Booking build() {
            if (checkIn == null || checkOut == null) {
                throw new IllegalArgumentException("Check-in and check-out dates are required.");
            }
            if (!checkOut.isAfter(checkIn)) {
                throw new IllegalArgumentException("Check-out must be after check-in.");
            }
            if (numberOfRenters < 1) {
                throw new IllegalArgumentException("At least one guest is required.");
            }
            String in  = checkIn.format(DB_DATE) + " 14:00:00";
            String out = checkOut.format(DB_DATE) + " 12:00:00";
            return new Booking(
                    0, renterId, unitId,
                    null, in, out,
                    numberOfRenters,
                    (specialRequests == null || specialRequests.isBlank()) ? null : specialRequests,
                    "pending",
                    totalAmount,
                    null, null, null, null, null);
        }
    }
}
