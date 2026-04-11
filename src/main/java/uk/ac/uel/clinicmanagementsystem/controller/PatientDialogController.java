package uk.ac.uel.clinicmanagementsystem.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import uk.ac.uel.clinicmanagementsystem.model.Patient;

public class PatientDialogController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private DatePicker dateOfBirthPicker;

    @FXML
    private TextField phoneField;

    public void setPatient(Patient patient) {
        firstNameField.setText(patient.firstName());
        lastNameField.setText(patient.lastName());
        dateOfBirthPicker.setValue(patient.dateOfBirth());
        phoneField.setText(patient.phone());
    }

    public Patient.Create getPatient() {
        return new Patient.Create(
            firstNameField.getText(),
            lastNameField.getText(),
            dateOfBirthPicker.getValue(),
            phoneField.getText()
        );
    }
}
