module uk.ac.uel.clinicmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;

    opens uk.ac.uel.clinicmanagementsystem to javafx.fxml;
    exports uk.ac.uel.clinicmanagementsystem;
    exports uk.ac.uel.clinicmanagementsystem.controller;
    opens uk.ac.uel.clinicmanagementsystem.controller to javafx.fxml;
    exports uk.ac.uel.clinicmanagementsystem.model;
    exports uk.ac.uel.clinicmanagementsystem.service;
    exports uk.ac.uel.clinicmanagementsystem.repository;
    exports uk.ac.uel.clinicmanagementsystem.util;
    opens uk.ac.uel.clinicmanagementsystem.util to javafx.fxml;
}
