package uk.ac.uel.clinicmanagementsystem.repository.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import uk.ac.uel.clinicmanagementsystem.model.Appointment;
import uk.ac.uel.clinicmanagementsystem.repository.AppointmentRepository;
import uk.ac.uel.clinicmanagementsystem.util.AppException;

/**
 * CSV implementation of {@link AppointmentRepository}.
 * Reads and rewrites at {@code data/appointments.csv}.
 */
public class CSVAppointmentRepository implements AppointmentRepository {

    private static final Path filePath = Path.of(
        System.getProperty("user.home"),
        "ClinicManagementSystem",
        "appointments.csv"
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Appointment> findAll() {
        return readAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Appointment create(Appointment.Create create) {
        List<Appointment> appointments = readAll();
        Appointment created = new Appointment(
            UUID.randomUUID().toString(),
            create.doctorId(),
            create.patientId(),
            create.date(),
            create.time()
        );
        appointments.add(created);
        writeAll(appointments);
        return created;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Appointment update(String id, Appointment.Update update) {
        List<Appointment> appointments = readAll();
        Appointment updated = appointments
            .stream()
            .filter(a -> a.id().equals(id))
            .findFirst()
            .map(a ->
                new Appointment(
                    id,
                    update.doctorId().orElse(a.doctorId()),
                    update.patientId().orElse(a.patientId()),
                    update.date().orElse(a.date()),
                    update.time().orElse(a.time())
                )
            )
            .orElseThrow(() ->
                new AppException("Appointment not found: " + id, null)
            );
        writeAll(
            appointments
                .stream()
                .map(a -> a.id().equals(id) ? updated : a)
                .toList()
        );
        return updated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        List<Appointment> appointments = readAll();
        boolean removed = appointments.removeIf(a -> a.id().equals(id));
        if (removed) {
            writeAll(appointments);
        }
    }

    private List<Appointment> readAll() {
        if (!Files.exists(filePath)) return new ArrayList<>();

        try {
            return Files.readAllLines(filePath)
                .stream()
                .map(line -> {
                    String[] tokens = line.split(",");
                    return new Appointment(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        LocalDate.parse(tokens[3]),
                        LocalTime.parse(tokens[4])
                    );
                })
                .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new AppException("Failed to read appointments", e);
        }
    }

    private void writeAll(List<Appointment> appointments) {
        List<String> lines = appointments
            .stream()
            .map(appointment ->
                String.join(
                    ",",
                    appointment.id(),
                    appointment.doctorId(),
                    appointment.patientId(),
                    appointment.date().toString(),
                    appointment.time().toString()
                )
            )
            .toList();
        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new AppException("Failed to write appointments", e);
        }
    }
}
