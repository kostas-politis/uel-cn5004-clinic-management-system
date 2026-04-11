package uk.ac.uel.clinicmanagementsystem.service;

import java.util.List;
import uk.ac.uel.clinicmanagementsystem.model.Patient;
import uk.ac.uel.clinicmanagementsystem.repository.AppointmentRepository;
import uk.ac.uel.clinicmanagementsystem.repository.PatientRepository;

/**
 * Business logic for managing patients.
 */
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientService(
        PatientRepository patientRepository,
        AppointmentRepository appointmentRepository
    ) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Returns all patients.
     */
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    /**
     * Creates a new patient and returns the new record.
     *
     * @param create the creation data
     * @return the created {@link Patient}
     */
    public Patient create(Patient.Create create) {
        return patientRepository.create(create);
    }

    /**
     * Updates an existing patient and returns the updated record.
     *
     * @param patientId the id of the patient to update
     * @param update patient fields to update
     * @return the updated {@link Patient}
     */
    public Patient update(String patientId, Patient.Update update) {
        return patientRepository.update(patientId, update);
    }

    /**
     * Deletes a patient and all their appointments.
     *
     * @param patientId the id of the patient to delete
     */
    public void delete(String patientId) {
        appointmentRepository
            .findAll()
            .stream()
            .filter(a -> a.patientId().equals(patientId))
            .forEach(a -> appointmentRepository.delete(a.id()));
        patientRepository.delete(patientId);
    }
}
