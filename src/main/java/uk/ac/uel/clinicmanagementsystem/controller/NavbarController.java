package uk.ac.uel.clinicmanagementsystem.controller;

import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import uk.ac.uel.clinicmanagementsystem.util.ViewManager;
import uk.ac.uel.clinicmanagementsystem.util.ViewManager.View;

public class NavbarController {

    @FXML
    private Button appointmentsBtn;

    @FXML
    private Button doctorsBtn;

    @FXML
    private Button patientsBtn;

    @FXML
    public void initialize() {
        Map<View, Button> viewButtons = Map.of(
            View.APPOINTMENTS,
            appointmentsBtn,
            View.DOCTORS,
            doctorsBtn,
            View.PATIENTS,
            patientsBtn
        );
        Button active = viewButtons.get(
            ViewManager.getInstance().getCurrentView()
        );
        if (active != null) active.getStyleClass().add("active");
    }

    @FXML
    public void onAppointmentsClicked() {
        ViewManager.getInstance().navigateTo(View.APPOINTMENTS);
    }

    @FXML
    public void onDoctorsClicked() {
        ViewManager.getInstance().navigateTo(View.DOCTORS);
    }

    @FXML
    public void onPatientsClicked() {
        ViewManager.getInstance().navigateTo(View.PATIENTS);
    }
}
