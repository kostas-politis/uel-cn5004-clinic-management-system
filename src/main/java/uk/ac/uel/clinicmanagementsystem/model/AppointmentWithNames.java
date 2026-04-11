package uk.ac.uel.clinicmanagementsystem.model;

/**
 * An {@link Appointment} with doctor and patient display names.
 */
public record AppointmentWithNames(
    Appointment source,
    String doctorName,
    String patientName
) {}
