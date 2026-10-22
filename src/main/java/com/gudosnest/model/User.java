package com.gudosnest.model;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private boolean owner;
    private boolean renter;
    private String accountStatus;

    public User(int userId, String fullName, String email,
            String passwordHash, boolean owner, boolean renter,
            String accountStatus) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.owner = owner;
        this.renter = renter;
        this.accountStatus = accountStatus;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isOwner() {
        return owner;
    }

    public boolean isRenter() {
        return renter;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public boolean isActive() {
        return "active".equals(accountStatus);
    }
}