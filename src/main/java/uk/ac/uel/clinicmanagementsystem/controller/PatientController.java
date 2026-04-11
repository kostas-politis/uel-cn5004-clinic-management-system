package uk.ac.uel.clinicmanagementsystem.controller;

import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import uk.ac.uel.clinicmanagementsystem.App;
import uk.ac.uel.clinicmanagementsystem.model.Patient;
import uk.ac.uel.clinicmanagementsystem.service.PatientService;
import uk.ac.uel.clinicmanagementsystem.util.ActionButtonTableCell;
import uk.ac.uel.clinicmanagementsystem.util.AppException;

public class PatientController {

    private final PatientService patientService;
    private final ObservableList<Patient> patients =
        FXCollections.observableArrayList();

    @FXML
    private BorderPane mainPane;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Patient> patientsTable;

    @FXML
    private TableColumn<Patient, String> idColumn;

    @FXML
    private TableColumn<Patient, String> firstNameColumn;

    @FXML
    private TableColumn<Patient, String> lastNameColumn;

    @FXML
    private TableColumn<Patient, String> dateOfBirthColumn;

    @FXML
    private TableColumn<Patient, String> phoneColumn;

    @FXML
    private TableColumn<Patient, Void> editColumn;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().id())
        );
        firstNameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().firstName())
        );
        lastNameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().lastName())
        );
        dateOfBirthColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().dateOfBirth().toString())
        );
        phoneColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().phone())
        );
        editColumn.setCellFactory(
            ActionButtonTableCell.forTableColumn(
                "✎",
                this::openEditPatientDialog
            )
        );

        patients.setAll(patientService.findAll());
        FilteredList<Patient> filtered = new FilteredList<>(patients);
        patientsTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        patientsTable.setItems(filtered);

        searchField
            .textProperty()
            .addListener((_, _, newVal) ->
                filtered.setPredicate(patient -> {
                    if (newVal == null || newVal.isBlank()) return true;
                    String lower = newVal.toLowerCase();
                    return (
                        patient.id().toLowerCase().contains(lower) ||
                        patient.firstName().toLowerCase().contains(lower) ||
                        patient.lastName().toLowerCase().contains(lower) ||
                        patient
                            .dateOfBirth()
                            .toString()
                            .toLowerCase()
                            .contains(lower) ||
                        patient.phone().toLowerCase().contains(lower)
                    );
                })
            );
    }

    @FXML
    private void onNewPatientClicked() {
        openNewPatientDialog();
    }

    private void openEditPatientDialog(Patient existing) {
        // Load patient dialog fxml
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("components/patient-dialog.fxml")
        );
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new AppException("Failed to load patient dialog", e);
        }

        // Init dialog controller
        PatientDialogController controller = loader.getController();
        controller.setPatient(existing);

        // Init dialog and buttons
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setDialogPane(pane);
        dialog.setTitle("Edit Patient");

        ButtonType deleteBt = new ButtonType("Delete", ButtonData.LEFT);
        pane.getButtonTypes().add(deleteBt);

        pane
            .lookupButton(deleteBt)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                patientService.delete(existing.id());
                patients.remove(existing);
            });

        pane
            .lookupButton(ButtonType.OK)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                Patient.Create data = controller.getPatient();
                Patient updated = patientService.update(
                    existing.id(),
                    new Patient.Update(
                        Optional.of(data.firstName()),
                        Optional.of(data.lastName()),
                        Optional.of(data.dateOfBirth()),
                        Optional.of(data.phone())
                    )
                );
                patients.set(patients.indexOf(existing), updated);
            });

        dialog.showAndWait();
    }

    private void openNewPatientDialog() {
        // Load patient dialog fxml
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("components/patient-dialog.fxml")
        );
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new AppException("Failed to load patient dialog", e);
        }

        // Init dialog controller
        PatientDialogController controller = loader.getController();

        // Init dialog and buttons
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setDialogPane(pane);
        dialog.setTitle("New Patient");

        pane
            .lookupButton(ButtonType.OK)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                Patient.Create data = controller.getPatient();
                patients.add(patientService.create(data));
            });

        dialog.showAndWait();
    }
}
