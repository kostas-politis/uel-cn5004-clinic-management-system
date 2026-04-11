package uk.ac.uel.clinicmanagementsystem.util;

// Source - https://stackoverflow.com/a/76248931
// Posted by James_D, modified by community. See post 'Timeline' for change history
// Retrieved 2026-04-30, License - CC BY-SA 4.0

import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class ActionButtonTableCell<S, T> extends TableCell<S, T> {

    private final Button actionButton;

    public ActionButtonTableCell(String label, Consumer<S> function) {
        this.getStyleClass().add("action-button-table-cell");
        this.actionButton = new Button(label);
        this.actionButton.setOnAction(e -> function.accept(getCurrentItem()));
    }

    public static <S, T> Callback<
        TableColumn<S, T>,
        TableCell<S, T>
    > forTableColumn(String label, Consumer<S> function) {
        return param -> new ActionButtonTableCell<>(label, function);
    }

    public S getCurrentItem() {
        // No need for a cast here:
        return getTableView().getItems().get(getIndex());
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }
}
