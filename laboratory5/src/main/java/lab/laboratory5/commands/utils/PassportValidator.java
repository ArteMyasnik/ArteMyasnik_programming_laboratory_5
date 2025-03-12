package lab.laboratory5.commands.utils;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class PassportValidator {
    @Getter
    private final Set<String> passportValidator = new HashSet<>();

    public boolean isPasswordUnique(String password) {
        return !passportValidator.contains(password);
    }

    public void addPassword(String password) {
        if (isPasswordUnique(password)) {
            passportValidator.add(password);
        }
    }

    public boolean removePassword(String password) {
        return passportValidator.remove(password);
    }

}
