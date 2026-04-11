package uk.ac.uel.clinicmanagementsystem.repository;

import java.util.List;
import uk.ac.uel.clinicmanagementsystem.model.Patient;

/** Data access repository for {@link Patient} records. */
public interface PatientRepository {
    /** Returns all patients. */
    List<Patient> findAll();

    /**
     * Creates a new patient and returns the new record.
     *
     * @param create the creation data
     * @return the created {@link Patient}
     */
    Patient create(Patient.Create create);

    /**
     * Updates an existing patient and returns the updated record.
     *
     * @param patientId the id of the patient to update
     * @param update    patient fields to update
     * @return the updated {@link Patient}
     */
    Patient update(String patientId, Patient.Update update);

    /**
     * Deletes a patient.
     *
     * @param patientId the id of the patient to delete
     */
    void delete(String patientId);
}
