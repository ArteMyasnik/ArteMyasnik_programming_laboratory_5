package lab.laboratory5.commands.done;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PassportValidator {
    private final Set<String> passportValidator = new HashSet<>();

    public boolean isPasswordUnique(String password) {
        return !passportValidator.contains(password);
    }

    public void addPassword(String password) {
        if (isPasswordUnique(password)) {
            passportValidator.add(password);
        }
    }

    public void removePassword(String password) {
        passportValidator.remove(password);
    }

}
