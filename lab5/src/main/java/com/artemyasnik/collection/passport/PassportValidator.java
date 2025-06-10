package com.artemyasnik.collection.passport;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class PassportValidator {
    private static PassportValidator INSTANCE;
    private final Set<String> passportIDs;

    private PassportValidator() {
        passportIDs = new HashSet<>();
    }

    public static PassportValidator getInstance() {
        return INSTANCE == null ? INSTANCE = new PassportValidator() : INSTANCE;
    }

    public boolean validate(String passportID) {
        if (passportID == null || passportID.isEmpty()) {
            throw new IllegalArgumentException("PassportID can't be null or empty");
        }

        if (passportIDs.contains(passportID)) {
            throw new IllegalArgumentException("PassportID must be unique: " + passportID);
        }

        add(passportID);
        return true;
    }

    private void add(String passportID) {
        getPassportIDs().add(passportID);
    }

    public void remove(String passportID) {
        getPassportIDs().remove(passportID);
    }

    public void clear() {
        getPassportIDs().clear();
    }
}