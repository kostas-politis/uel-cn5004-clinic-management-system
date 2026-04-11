package uk.ac.uel.clinicmanagementsystem.repository.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import uk.ac.uel.clinicmanagementsystem.model.Patient;
import uk.ac.uel.clinicmanagementsystem.repository.PatientRepository;
import uk.ac.uel.clinicmanagementsystem.util.AppException;

/**
 * CSV implementation of {@link PatientRepository}.
 * Reads and rewrites at {@code data/patients.csv}.
 */
public class CSVPatientRepository implements PatientRepository {

    private static final Path filePath = Path.of(
        System.getProperty("user.home"),
        "ClinicManagementSystem",
        "patients.csv"
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Patient> findAll() {
        return readAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Patient create(Patient.Create create) {
        List<Patient> patients = readAll();
        Patient created = new Patient(
            UUID.randomUUID().toString(),
            create.firstName(),
            create.lastName(),
            create.dateOfBirth(),
            create.phone()
        );
        patients.add(created);
        writeAll(patients);
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Patient update(String id, Patient.Update update) {
        List<Patient> patients = readAll();
        Patient updated = patients
            .stream()
            .filter(p -> p.id().equals(id))
            .findFirst()
            .map(p ->
                new Patient(
                    id,
                    update.firstName().orElse(p.firstName()),
                    update.lastName().orElse(p.lastName()),
                    update.dateOfBirth().orElse(p.dateOfBirth()),
                    update.phone().orElse(p.phone())
                )
            )
            .orElseThrow(() ->
                new AppException("Patient not found: " + id, null)
            );
        writeAll(
            patients
                .stream()
                .map(p -> p.id().equals(id) ? updated : p)
                .toList()
        );
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        List<Patient> patients = readAll();
        boolean removed = patients.removeIf(p -> p.id().equals(id));
        if (removed) {
            writeAll(patients);
        }
    }

    private List<Patient> readAll() {
        if (!Files.exists(filePath)) return new ArrayList<>();

        try {
            return Files.readAllLines(filePath)
                .stream()
                .map(line -> {
                    String[] tokens = line.split(",");
                    return new Patient(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        LocalDate.parse(tokens[3]),
                        tokens[4]
                    );
                })
                .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new AppException("Failed to read patients", e);
        }
    }

    private void writeAll(List<Patient> patients) {
        List<String> lines = patients
            .stream()
            .map(patient ->
                String.join(
                    ",",
                    patient.id(),
                    patient.firstName(),
                    patient.lastName(),
                    patient.dateOfBirth().toString(),
                    patient.phone()
                )
            )
            .toList();
        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new AppException("Failed to write patients", e);
        }
    }
}
