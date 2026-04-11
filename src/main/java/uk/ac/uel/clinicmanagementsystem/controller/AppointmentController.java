package uk.ac.uel.clinicmanagementsystem.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
import uk.ac.uel.clinicmanagementsystem.model.Appointment;
import uk.ac.uel.clinicmanagementsystem.model.AppointmentWithNames;
import uk.ac.uel.clinicmanagementsystem.model.Doctor;
import uk.ac.uel.clinicmanagementsystem.model.Patient;
import uk.ac.uel.clinicmanagementsystem.service.AppointmentService;
import uk.ac.uel.clinicmanagementsystem.service.DoctorService;
import uk.ac.uel.clinicmanagementsystem.service.PatientService;
import uk.ac.uel.clinicmanagementsystem.util.ActionButtonTableCell;
import uk.ac.uel.clinicmanagementsystem.util.AppException;

public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    private List<Doctor> doctorList;
    private Map<String, String> doctorNames; // Map[doctorId][FirstName LastName]
    private List<Patient> patientList;
    private Map<String, String> patientNames; // Map[patientId][FirstName LastName]

    private final ObservableList<AppointmentWithNames> appointmentList =
        FXCollections.observableArrayList();

    // This list is the source of truth for the UI
    private final FilteredList<AppointmentWithNames> filteredAppointmentList =
        new FilteredList<>(appointmentList);

    @FXML
    private BorderPane mainPane;

    @FXML
    private TextField searchField;

    @FXML
    private ChoiceBox<String> dateChoiceBox;

    @FXML
    private TableView<AppointmentWithNames> appointmentsTable;

    @FXML
    private TableColumn<AppointmentWithNames, String> idColumn;

    @FXML
    private TableColumn<AppointmentWithNames, String> doctorColumn;

    @FXML
    private TableColumn<AppointmentWithNames, String> patientColumn;

    @FXML
    private TableColumn<AppointmentWithNames, String> dateColumn;

    @FXML
    private TableColumn<AppointmentWithNames, String> timeColumn;

    @FXML
    private TableColumn<AppointmentWithNames, Void> editColumn;

    public AppointmentController(
        AppointmentService appointmentService,
        DoctorService doctorService,
        PatientService patientService
    ) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    @FXML
    public void initialize() {
        initializeData();
        initializeTableView();
        initializeSearchBar();
        initializeDateFilter();
    }

    @FXML
    private void onNewAppointmentClicked() {
        openNewAppointmentDialog();
    }

    private void initializeData() {
        doctorList = doctorService.findAll();
        doctorNames = doctorList
            .stream()
            .collect(
                Collectors.toMap(
                    Doctor::id,
                    d -> d.firstName() + " " + d.lastName()
                )
            );

        patientList = patientService.findAll();
        patientNames = patientList
            .stream()
            .collect(
                Collectors.toMap(
                    Patient::id,
                    p -> p.firstName() + " " + p.lastName()
                )
            );

        appointmentList.setAll(
            appointmentService
                .findAll()
                .stream()
                .map(appointment ->
                    new AppointmentWithNames(
                        appointment,
                        doctorNames.get(appointment.doctorId()),
                        patientNames.get(appointment.patientId())
                    )
                )
                .toList()
        );
    }

    private void initializeTableView() {
        idColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().source().id())
        );
        doctorColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().doctorName())
        );
        patientColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().patientName())
        );
        dateColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().source().date().toString())
        );
        timeColumn.setCellValueFactory(cell ->
            new SimpleStringProperty(cell.getValue().source().time().toString())
        );
        editColumn.setCellFactory(
            ActionButtonTableCell.forTableColumn(
                "✎",
                this::openEditAppointmentDialog
            )
        );

        appointmentsTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_FLEX_NEXT_COLUMN
        );

        appointmentsTable.setItems(filteredAppointmentList);
    }

    private void initializeSearchBar() {
        searchField.textProperty().addListener((_, _, _) -> updateFilter());
    }

    private void initializeDateFilter() {
        dateChoiceBox.getItems().addAll("All Time", "Today", "Tomorrow");
        dateChoiceBox.setValue("All Time");
        dateChoiceBox.valueProperty().addListener((_, _, _) -> updateFilter());
    }

    private void updateFilter() {
        String searchText = searchField.getText();
        String dateFilter = dateChoiceBox.getValue();
        LocalDate today = LocalDate.now();

        filteredAppointmentList.setPredicate(a -> {
            boolean matchesDate = switch (dateFilter) {
                case "Today" -> a.source().date().equals(today);
                case "Tomorrow" -> a.source().date().equals(today.plusDays(1));
                default -> true;
            };
            if (!matchesDate) {
                return false;
            }
            if (searchText == null || searchText.isBlank()) {
                return true;
            }
            String lower = searchText.toLowerCase();
            return (
                a.source().id().toLowerCase().contains(lower) ||
                a.source().date().toString().contains(lower) ||
                a.source().time().toString().contains(lower) ||
                a.doctorName().toLowerCase().contains(lower) ||
                a.patientName().toLowerCase().contains(lower)
            );
        });
    }

    private void openEditAppointmentDialog(AppointmentWithNames existing) {
        // Load appointment dialog fxml
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("components/appointment-dialog.fxml")
        );
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new AppException("Failed to load appointment dialog", e);
        }

        // Init dialog controller
        AppointmentDialogController controller = loader.getController();
        controller.setDoctors(doctorList);
        controller.setPatients(patientList);
        controller.setAppointment(existing.source());

        // Init dialog and buttons
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setDialogPane(pane);
        dialog.setTitle("Edit Appointment");

        ButtonType deleteBt = new ButtonType("Delete", ButtonData.LEFT);
        pane.getButtonTypes().add(deleteBt);

        // Register delete event handler
        pane
            .lookupButton(deleteBt)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                appointmentService.delete(existing.source().id());
                appointmentList.remove(existing);
            });

        // Register edit event handler
        pane
            .lookupButton(ButtonType.OK)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                Appointment.Create data = controller.getAppointment();
                Appointment updated = appointmentService.update(
                    existing.source().id(),
                    new Appointment.Update(
                        Optional.of(data.doctorId()),
                        Optional.of(data.patientId()),
                        Optional.of(data.date()),
                        Optional.of(data.time())
                    )
                );
                appointmentList.set(
                    appointmentList.indexOf(existing),
                    new AppointmentWithNames(
                        updated,
                        doctorNames.get(updated.doctorId()),
                        patientNames.get(updated.patientId())
                    )
                );
            });

        dialog.showAndWait();
    }

    private void openNewAppointmentDialog() {
        // Load appointment dialog fxml
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("components/appointment-dialog.fxml")
        );
        DialogPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            throw new AppException("Failed to load appointment dialog", e);
        }

        // Init dialog controller
        AppointmentDialogController controller = loader.getController();
        controller.setDoctors(doctorList);
        controller.setPatients(patientList);

        // Init dialog and buttons
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setDialogPane(pane);
        dialog.setTitle("New Appointment");

        // Register event handler
        pane
            .lookupButton(ButtonType.OK)
            .addEventFilter(ActionEvent.ACTION, _ -> {
                Appointment.Create data = controller.getAppointment();
                Appointment created = appointmentService.create(data);
                appointmentList.add(
                    new AppointmentWithNames(
                        created,
                        doctorNames.get(created.doctorId()),
                        patientNames.get(created.patientId())
                    )
                );
            });

        dialog.showAndWait();
    }
}
