package uk.ac.uel.clinicmanagementsystem.controller;

import java.time.LocalTime;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import uk.ac.uel.clinicmanagementsystem.model.Appointment;
import uk.ac.uel.clinicmanagementsystem.model.Doctor;
import uk.ac.uel.clinicmanagementsystem.model.Patient;

public class AppointmentDialogController {

    @FXML
    private ComboBox<Doctor> doctorComboBox;

    @FXML
    private ComboBox<Patient> patientComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    public void initialize() {
        hourSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0)
        );
        minuteSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0)
        );
    }

    public void setDoctors(List<Doctor> doctors) {
        doctorComboBox.setItems(FXCollections.observableArrayList(doctors));
        StringConverter<Doctor> converter = new StringConverter<>() {
            @Override
            public String toString(Doctor d) {
                return d == null
                    ? ""
                    : d.firstName() + " " + d.lastName() + " (" + d.id() + ")";
            }

            @Override
            public Doctor fromString(String s) {
                return null;
            }
        };
        doctorComboBox.setConverter(converter);
        doctorComboBox.setCellFactory(_ ->
            new ListCell<>() {
                @Override
                protected void updateItem(Doctor d, boolean empty) {
                    super.updateItem(d, empty);
                    setText(
                        empty || d == null
                            ? null
                            : d.firstName() +
                              " " +
                              d.lastName() +
                              " (" +
                              d.id() +
                              ")"
                    );
                }
            }
        );
    }

    public void setPatients(List<Patient> patients) {
        patientComboBox.setItems(FXCollections.observableArrayList(patients));
        StringConverter<Patient> converter = new StringConverter<>() {
            @Override
            public String toString(Patient p) {
                return p == null
                    ? ""
                    : p.firstName() + " " + p.lastName() + " (" + p.id() + ")";
            }

            @Override
            public Patient fromString(String s) {
                return null;
            }
        };
        patientComboBox.setConverter(converter);
        patientComboBox.setCellFactory(_ ->
            new ListCell<>() {
                @Override
                protected void updateItem(Patient p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(
                        empty || p == null
                            ? null
                            : p.firstName() +
                              " " +
                              p.lastName() +
                              " (" +
                              p.id() +
                              ")"
                    );
                }
            }
        );
    }

    public void setAppointment(Appointment appointment) {
        doctorComboBox
            .getItems()
            .stream()
            .filter(d -> d.id().equals(appointment.doctorId()))
            .findFirst()
            .ifPresent(doctorComboBox::setValue);
        patientComboBox
            .getItems()
            .stream()
            .filter(p -> p.id().equals(appointment.patientId()))
            .findFirst()
            .ifPresent(patientComboBox::setValue);
        datePicker.setValue(appointment.date());
        hourSpinner.getValueFactory().setValue(appointment.time().getHour());
        minuteSpinner
            .getValueFactory()
            .setValue(appointment.time().getMinute());
    }

    public Appointment.Create getAppointment() {
        return new Appointment.Create(
            doctorComboBox.getValue().id(),
            patientComboBox.getValue().id(),
            datePicker.getValue(),
            LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue())
        );
    }
}
