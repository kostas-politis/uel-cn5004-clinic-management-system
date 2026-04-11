package uk.ac.uel.clinicmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uk.ac.uel.clinicmanagementsystem.model.Doctor;

public class DoctorDialogController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField specialtyField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label errorLabel;

    public void setDoctor(Doctor doctor) {
        firstNameField.setText(doctor.firstName());
        lastNameField.setText(doctor.lastName());
        specialtyField.setText(doctor.specialty());
        phoneField.setText(doctor.phone());
    }

    public Doctor.Create getDoctor() {
        return new Doctor.Create(
            firstNameField.getText(),
            lastNameField.getText(),
            specialtyField.getText(),
            phoneField.getText()
        );
    }
}
