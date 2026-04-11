package uk.ac.uel.clinicmanagementsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import uk.ac.uel.clinicmanagementsystem.controller.AppointmentController;
import uk.ac.uel.clinicmanagementsystem.controller.DoctorController;
import uk.ac.uel.clinicmanagementsystem.controller.NavbarController;
import uk.ac.uel.clinicmanagementsystem.controller.PatientController;
import uk.ac.uel.clinicmanagementsystem.repository.csv.CSVAppointmentRepository;
import uk.ac.uel.clinicmanagementsystem.repository.csv.CSVDoctorRepository;
import uk.ac.uel.clinicmanagementsystem.repository.csv.CSVPatientRepository;
import uk.ac.uel.clinicmanagementsystem.service.AppointmentService;
import uk.ac.uel.clinicmanagementsystem.service.DoctorService;
import uk.ac.uel.clinicmanagementsystem.service.PatientService;
import uk.ac.uel.clinicmanagementsystem.util.AppException;
import uk.ac.uel.clinicmanagementsystem.util.ViewManager;
import uk.ac.uel.clinicmanagementsystem.util.ViewManager.View;

public class App extends Application {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage stage) {
        // initialize a global error handler
        Thread.currentThread().setUncaughtExceptionHandler((_, e) -> {
            AppException appEx =
                e instanceof AppException ae
                    ? ae
                    : new AppException("An unexpected error occurred.", e);
            logger.log(Level.SEVERE, appEx.getMessage(), appEx);
            Alert alert = new Alert(
                Alert.AlertType.ERROR,
                appEx.getMessage(),
                ButtonType.OK
            );
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.showAndWait();
        });

        // ensure data directory exists
        try {
            Files.createDirectories(
                Path.of(
                    System.getProperty("user.home"),
                    "ClinicManagementSystem"
                )
            );
        } catch (IOException e) {
            throw new AppException("Failed to create data directory", e);
        }

        // initialize repositories (data access)
        CSVDoctorRepository doctorRepository = new CSVDoctorRepository();
        CSVPatientRepository patientRepository = new CSVPatientRepository();
        CSVAppointmentRepository appointmentRepository =
            new CSVAppointmentRepository();

        // initialize services (business logic)
        DoctorService doctorService = new DoctorService(
            doctorRepository,
            appointmentRepository
        );
        PatientService patientService = new PatientService(
            patientRepository,
            appointmentRepository
        );
        AppointmentService appointmentService = new AppointmentService(
            appointmentRepository
        );

        ViewManager.initialize(stage, clazz -> {
            if (clazz == NavbarController.class) {
                return new NavbarController();
            }
            if (clazz == DoctorController.class) {
                return new DoctorController(doctorService);
            }
            if (clazz == PatientController.class) {
                return new PatientController(patientService);
            }
            if (clazz == AppointmentController.class) {
                return new AppointmentController(
                    appointmentService,
                    doctorService,
                    patientService
                );
            }
            throw new IllegalArgumentException("Unknown controller: " + clazz);
        });

        stage.setTitle("Clinic Management System");
        ViewManager.getInstance().navigateTo(View.APPOINTMENTS);
        stage.show();
    }
}
