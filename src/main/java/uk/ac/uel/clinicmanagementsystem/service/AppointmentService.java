package uk.ac.uel.clinicmanagementsystem.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import uk.ac.uel.clinicmanagementsystem.model.Appointment;
import uk.ac.uel.clinicmanagementsystem.repository.AppointmentRepository;
import uk.ac.uel.clinicmanagementsystem.util.AppException;

/**
 * Business logic for managing appointments.
 */
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Returns all appointments.
     */
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    /**
     * Creates a new appointment and returns the new record.
     *
     * @param create the creation data
     * @return the created {@link Appointment}
     * @throws AppException if the doctor is already booked at that date and time
     */
    public Appointment create(Appointment.Create create) {
        if (
            isDoubleBooked(
                create.doctorId(),
                create.date(),
                create.time(),
                null
            )
        ) {
            throw new AppException(
                "This doctor already has an appointment at that date and time.",
                null
            );
        }
        return appointmentRepository.create(create);
    }

    /**
     * Updates an existing appointment and returns the updated record.
     *
     * @param appointmentId the id of the appointment to update
     * @param update        appointment fields to update
     * @return the updated {@link Appointment}
     * @throws AppException if the doctor is already booked at the resolved date and time
     */
    public Appointment update(String appointmentId, Appointment.Update update) {
        Appointment existing = appointmentRepository
            .findAll()
            .stream()
            .filter(a -> a.id().equals(appointmentId))
            .findFirst()
            .orElseThrow(() ->
                new AppException(
                    "Appointment not found: " + appointmentId,
                    null
                )
            );

        String resolvedDoctorId = update.doctorId().orElse(existing.doctorId());
        LocalDate resolvedDate = update.date().orElse(existing.date());
        LocalTime resolvedTime = update.time().orElse(existing.time());

        if (
            isDoubleBooked(
                resolvedDoctorId,
                resolvedDate,
                resolvedTime,
                appointmentId
            )
        ) {
            throw new AppException(
                "This doctor already has an appointment at that date and time.",
                null
            );
        }
        return appointmentRepository.update(appointmentId, update);
    }

    /**
     * Deletes an appointment.
     *
     * @param appointmentId the id of the appointment to delete
     */
    public void delete(String appointmentId) {
        appointmentRepository.delete(appointmentId);
    }

    /**
     * Returns {@code true} if the doctor already has an appointment at the given date and time,
     */
    private boolean isDoubleBooked(
        String doctorId,
        LocalDate date,
        LocalTime time,
        String appointmentIdBeingEdited
    ) {
        return appointmentRepository
            .findAll()
            .stream()
            .filter(a -> !a.id().equals(appointmentIdBeingEdited))
            .filter(a -> a.doctorId().equals(doctorId))
            .filter(a -> a.date().equals(date))
            .anyMatch(
                a ->
                    time.isBefore(a.time().plusMinutes(30)) &&
                    a.time().isBefore(time.plusMinutes(30))
            );
    }
}
