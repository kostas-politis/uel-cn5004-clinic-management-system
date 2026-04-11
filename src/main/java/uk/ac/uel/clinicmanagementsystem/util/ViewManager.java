package uk.ac.uel.clinicmanagementsystem.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import uk.ac.uel.clinicmanagementsystem.App;

/**
 * Singleton that owns the primary {@link Stage} and handles scene switching.
 */
public class ViewManager {

    private static ViewManager instance;

    private final Stage stage;
    private final Callback<Class<?>, Object> controllerFactory;
    private View currentView;

    private ViewManager(
        Stage stage,
        Callback<Class<?>, Object> controllerFactory
    ) {
        this.stage = stage;
        this.controllerFactory = controllerFactory;
    }

    /**
     * Initializes the singleton. Must be called once at application startup.
     */
    public static void initialize(
        Stage stage,
        Callback<Class<?>, Object> controllerFactory
    ) {
        instance = new ViewManager(stage, controllerFactory);
    }

    /**
     * Returns the singleton instance.
     */
    public static ViewManager getInstance() {
        if (instance == null) throw new AppException(
            "ViewManager not initialized"
        );
        return instance;
    }

    /**
     * Returns the currently displayed view.
     */
    public View getCurrentView() {
        return currentView;
    }

    /**
     * Loads the FXML for the given view and sets it as the active scene.
     */
    public void navigateTo(View view) {
        String path = "views/" + view.name().toLowerCase() + ".fxml";
        FXMLLoader loader = new FXMLLoader(App.class.getResource(path));
        loader.setControllerFactory(controllerFactory);
        currentView = view;
        Parent content;
        try {
            content = loader.load();
        } catch (IOException e) {
            throw new AppException("Failed to load view: " + view, e);
        }

        Scene scene = new Scene(content, 900, 600);
        scene
            .getStylesheets()
            .addAll(
                App.class.getResource("styles/palette.css").toString(),
                App.class.getResource("styles/controls.css").toString(),
                App.class.getResource("styles/styles.css").toString()
            );
        stage.setScene(scene);
    }

    public enum View {
        APPOINTMENTS,
        DOCTORS,
        PATIENTS,
    }
}
