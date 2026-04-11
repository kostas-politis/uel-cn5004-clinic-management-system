package uk.ac.uel.clinicmanagementsystem.repository;

import java.util.List;
import uk.ac.uel.clinicmanagementsystem.model.Appointment;

/** Data access repository for {@link Appointment} records. */
public interface AppointmentRepository {
    /** Returns all appointments. */
    List<Appointment> findAll();

    /**
     * Creates a new appointment and returns the new record.
     *
     * @param create the creation data
     * @return the created {@link Appointment}
     */
    Appointment create(Appointment.Create create);

    /**
     * Updates an existing appointment and returns the updated record.
     *
     * @param appointmentId the id of the appointment to update
     * @param update appointment fields to update
     * @return the updated {@link Appointment}
     */
    Appointment update(String appointmentId, Appointment.Update update);

    /**
     * Deletes an appointment.
     *
     * @param appointmentId the id of the appointment to delete
     */
    void delete(String appointmentId);
}
