package uk.ac.uel.clinicmanagementsystem.repository.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import uk.ac.uel.clinicmanagementsystem.model.Doctor;
import uk.ac.uel.clinicmanagementsystem.repository.DoctorRepository;
import uk.ac.uel.clinicmanagementsystem.util.AppException;

/**
 * CSV implementation of {@link DoctorRepository}.
 * Reads and rewrites at {@code data/doctors.csv}.
 */
public class CSVDoctorRepository implements DoctorRepository {

    private static final Path filePath = Path.of(
        System.getProperty("user.home"),
        "ClinicManagementSystem",
        "doctors.csv"
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Doctor> findAll() {
        return readAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Doctor create(Doctor.Create create) {
        List<Doctor> doctors = readAll();
        Doctor created = new Doctor(
            UUID.randomUUID().toString(),
            create.firstName(),
            create.lastName(),
            create.specialty(),
            create.phone()
        );
        doctors.add(created);
        writeAll(doctors);
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Doctor update(String id, Doctor.Update update) {
        List<Doctor> doctors = readAll();
        Doctor updated = doctors
            .stream()
            .filter(d -> d.id().equals(id))
            .findFirst()
            .map(d ->
                new Doctor(
                    id,
                    update.firstName().orElse(d.firstName()),
                    update.lastName().orElse(d.lastName()),
                    update.specialty().orElse(d.specialty()),
                    update.phone().orElse(d.phone())
                )
            )
            .orElseThrow(() ->
                new AppException("Doctor not found: " + id, null)
            );
        writeAll(
            doctors
                .stream()
                .map(d -> d.id().equals(id) ? updated : d)
                .toList()
        );
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        List<Doctor> doctors = readAll();
        boolean removed = doctors.removeIf(d -> d.id().equals(id));
        if (removed) {
            writeAll(doctors);
        }
    }

    private List<Doctor> readAll() {
        if (!Files.exists(filePath)) return new ArrayList<>();

        try {
            return Files.readAllLines(filePath)
                .stream()
                .map(line -> {
                    String[] tokens = line.split(",");
                    return new Doctor(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        tokens[3],
                        tokens[4]
                    );
                })
                .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new AppException("Failed to read doctors", e);
        }
    }

    private void writeAll(List<Doctor> doctors) {
        List<String> lines = doctors
            .stream()
            .map(doctor ->
                String.join(
                    ",",
                    doctor.id(),
                    doctor.firstName(),
                    doctor.lastName(),
                    doctor.specialty(),
                    doctor.phone()
                )
            )
            .toList();
        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new AppException("Failed to write doctors", e);
        }
    }
}
