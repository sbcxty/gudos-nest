package com.gudosnest.model;

/**
 * One row from the Messages table, plus the joined sender / receiver names
 * that list queries pull in for the UI.
 */
public class Message {
    private final int messageId;
    private final int senderId;
    private final int receiverId;
    private final Integer bookingId; // null = not tied to a booking
    private final String messageText;
    private final String sentAt;     // display string
    private final boolean read;
    private final String senderName;
    private final String receiverName;

    public Message(int messageId, int senderId, int receiverId, Integer bookingId,
                   String messageText, String sentAt, boolean read,
                   String senderName, String receiverName) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.bookingId = bookingId;
        this.messageText = messageText;
        this.sentAt = sentAt;
        this.read = read;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    public int getMessageId()    { return messageId; }
    public int getSenderId()     { return senderId; }
    public int getReceiverId()   { return receiverId; }
    public Integer getBookingId(){ return bookingId; }
    public String getMessageText(){ return messageText; }
    public String getSentAt()    { return sentAt; }
    public boolean isRead()      { return read; }
    public String getSenderName(){ return senderName; }
    public String getReceiverName(){ return receiverName; }

    /** The booking this message is tied to, or "—" if none. */
    public String bookingRef() {
        return bookingId == null ? "—" : "Booking #" + bookingId;
    }
}
