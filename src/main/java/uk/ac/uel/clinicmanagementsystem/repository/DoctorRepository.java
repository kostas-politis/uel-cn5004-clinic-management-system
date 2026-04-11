package uk.ac.uel.clinicmanagementsystem.repository;

import java.util.List;
import uk.ac.uel.clinicmanagementsystem.model.Doctor;

/** Data access repository for {@link Doctor} records. */
public interface DoctorRepository {
    /** Returns all doctors. */
    List<Doctor> findAll();

    /**
     * Creates a new doctor and returns the new record.
     *
     * @param create the creation data
     * @return the created {@link Doctor}
     */
    Doctor create(Doctor.Create create);

    /**
     * Updates an existing doctor and returns the updated record.
     *
     * @param doctorId the id of the doctor to update
     * @param update   doctor fields to update
     * @return the updated {@link Doctor}
     */
    Doctor update(String doctorId, Doctor.Update update);

    /**
     * Deletes a doctor.
     *
     * @param doctorId the id of the doctor to delete
     */
    void delete(String doctorId);
}
