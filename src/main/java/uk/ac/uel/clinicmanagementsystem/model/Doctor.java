package uk.ac.uel.clinicmanagementsystem.model;

import java.util.Optional;

/**
 * @param id        unique identifier
 * @param firstName first name
 * @param lastName  last name
 * @param specialty medical specialty
 * @param phone     contact phone number
 */
public record Doctor(
    String id,
    String firstName,
    String lastName,
    String specialty,
    String phone
) {
    public record Create(
        String firstName,
        String lastName,
        String specialty,
        String phone
    ) {}

    public record Update(
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<String> specialty,
        Optional<String> phone
    ) {}
}
