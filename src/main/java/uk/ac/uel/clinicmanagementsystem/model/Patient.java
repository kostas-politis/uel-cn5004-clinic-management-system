package uk.ac.uel.clinicmanagementsystem.model;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @param id          unique identifier
 * @param firstName   first name
 * @param lastName    last name
 * @param dateOfBirth date of birth
 * @param phone       contact phone number
 */
public record Patient(
    String id,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String phone
) {
    public record Create(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String phone
    ) {}

    public record Update(
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<LocalDate> dateOfBirth,
        Optional<String> phone
    ) {}
}
