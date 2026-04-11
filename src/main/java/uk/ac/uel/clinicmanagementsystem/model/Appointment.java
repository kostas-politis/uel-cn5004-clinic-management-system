package uk.ac.uel.clinicmanagementsystem.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 *
 * @param id        unique identifier
 * @param doctorId  ID of the assigned doctor
 * @param patientId ID of the patient
 * @param date      date of the appointment
 * @param time      time of the appointment
 */
public record Appointment(
    String id,
    String doctorId,
    String patientId,
    LocalDate date,
    LocalTime time
) {
    public record Create(
        String doctorId,
        String patientId,
        LocalDate date,
        LocalTime time
    ) {}

    public record Update(
        Optional<String> doctorId,
        Optional<String> patientId,
        Optional<LocalDate> date,
        Optional<LocalTime> time
    ) {}
}
