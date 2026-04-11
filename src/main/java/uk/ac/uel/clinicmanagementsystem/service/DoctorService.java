package uk.ac.uel.clinicmanagementsystem.service;

import java.util.List;
import uk.ac.uel.clinicmanagementsystem.model.Doctor;
import uk.ac.uel.clinicmanagementsystem.repository.AppointmentRepository;
import uk.ac.uel.clinicmanagementsystem.repository.DoctorRepository;

/**
 * Business logic for managing doctors.
 */
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorService(
        DoctorRepository doctorRepository,
        AppointmentRepository appointmentRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Returns all doctors.
     */
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    /**
     * Creates a new doctor and returns the new record.
     *
     * @param create the creation data
     * @return the created {@link Doctor}
     */
    public Doctor add(Doctor.Create create) {
        return doctorRepository.create(create);
    }

    /**
     * Updates an existing doctor and returns the updated record.
     *
     * @param doctorId the id of the doctor to update
     * @param update   doctor fields to update
     * @return the updated {@link Doctor}
     */
    public Doctor update(String doctorId, Doctor.Update update) {
        return doctorRepository.update(doctorId, update);
    }

    /**
     * Deletes a doctor and all their appointments.
     *
     * @param doctorId the id of the doctor to delete
     */
    public void delete(String doctorId) {
        appointmentRepository
            .findAll()
            .stream()
            .filter(a -> a.doctorId().equals(doctorId))
            .forEach(a -> appointmentRepository.delete(a.id()));
        doctorRepository.delete(doctorId);
    }
}
