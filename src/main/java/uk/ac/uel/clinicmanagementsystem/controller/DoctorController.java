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
import uk.ac.uel.clinicmanagementsystem.model.Doctor;
import uk.ac.uel.clinicmanagementsystem.service.DoctorService;
import uk.ac.uel.clinicmanagementsystem.util.ActionButtonTableCell;
import uk.ac.uel.clinicmanagementsystem.util.AppException;

public class DoctorController {

    private final DoctorService doctorService;
    private final ObservableList<Doctor> doctors =
        FXCollections.observableArrayList();

    @FXML
    private BorderPane mainPane;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Doctor> doctorsTable;

    @FXML
    private TableColumn<Doctor, String> idColumn;

    @FXML
    private TableColumn<Doctor, String> firstNameColumn;

    @FXML
    private TableColumn<Doctor, String> lastNameColumn;

    @FXML
    private TableColumn<Doctor, String> specialtyColumn;

    @FXML
    private TableColumn<Doctor, String> phoneColumn;

    @FXML
    private TableColumn<Doctor, Void> editColumn;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @FXML
    public void initialize() {
        // Init column factories
        idColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().id())
        );
        firstNameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().firstName())
        );
        lastNameColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().lastName())
        );
        specialtyColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().specialty())
        );
        phoneColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().phone())
        );
        editColumn.setCellFactory(
            ActionButtonTableCell.forTableColumn(
                "✎",
                this::openEditDoctorDialog
            )
        );

        // Populate doctors list from service
        doctors.setAll(doctorService.findAll());
        FilteredList<Doctor> filtered = new FilteredList<>(doctors);
        doctorsTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        doctorsTable.setItems(filtered);

        // Watch search field input and update doctors list
        searchField
            .textProperty()
            .addListener((_, _, newVal) ->
                filtered.setPredicate(doctor -> {
                    if (newVal == null || newVal.isBlank()) return true;
                    String lower = newVal.toLowerCase();
                    return (
                        doctor.id().toLowerCase().contains(lower) ||
                        doctor.firstName().toLowerCase().contains(lower) ||
                        doctor.lastName().toLowerCase().contains(lower) ||
                        doctor.specialty().toLowerCase().contains(lower) ||
                        doctor.phone().toLowerCase().contains(lower)
                    );
                })
            );
    }

    @FXML
    private void onNewDoctorClicked() {
        openNewDoctorDialog();
    }

    /**
     * Opens a dialog to edit a doctor.
     *
     * @param existing the doctor to edit or null to create a new one
     */
    private void openEditDoctorDialog(Doctor existing) {
        // Load doctor dialog fxml
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("components/doctor-dialog.fxml")
        );
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new AppException("Failed to load doctor dialog", e);
        }

        // Init dialog controller
        DoctorDialogController controller = loader.getController();
        controller.setDoctor(existing);

        // Init dialog and buttons
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setDialogPane(pane);
        dialog.setTitle("Edit Doctor");

        ButtonType deleteBt = new ButtonType("Delete", ButtonData.LEFT);
        pane.getButtonTypes().add(deleteBt);

        pane
            .lookupButton(deleteBt)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                doctorService.delete(existing.id());
                doctors.remove(existing);
            });

        pane
            .lookupButton(ButtonType.OK)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                Doctor.Create data = controller.getDoctor();
                Doctor updated = doctorService.update(
                    existing.id(),
                    new Doctor.Update(
                        Optional.of(data.firstName()),
                        Optional.of(data.lastName()),
                        Optional.of(data.specialty()),
                        Optional.of(data.phone())
                    )
                );
                doctors.set(doctors.indexOf(existing), updated);
            });

        dialog.showAndWait();
    }

    /**
     * Opens a dialog to create a doctor.
     **/
    private void openNewDoctorDialog() {
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("components/doctor-dialog.fxml")
        );
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new AppException("Failed to load doctor dialog", e);
        }
        DoctorDialogController controller = loader.getController();

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setDialogPane(pane);
        dialog.setTitle("New Doctor");

        pane
            .lookupButton(ButtonType.OK)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                Doctor.Create data = controller.getDoctor();
                doctors.add(doctorService.add(data));
            });

        dialog.showAndWait();
    }
}
