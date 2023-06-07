package com.portal.view;

import com.portal.model.Professor;
import com.portal.model.Subject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfessorTabView {

    Scene scene;

    TableView professorsTable;
    RadioButton professorsAddRadio;
    RadioButton professorsEditRadio;
    RadioButton professorsDeleteRadio;

    TextField professorIdField;
    TextField professorNameField;
    TextField professorPhoneNumField;
    DatePicker professorBirthdateField;
    TextField professorAddressField;
    TextField professorEmailField;
    CheckComboBox professorSubjectsCombo;

    public ProfessorTabView(Scene scene) {
        this.scene = scene;

        professorsTable = (TableView) scene.lookup("#professorsTable");

        professorsAddRadio = (RadioButton) scene.lookup("#professorsAddRadio");
        professorsEditRadio = (RadioButton) scene.lookup("#professorsEditRadio");
        professorsDeleteRadio = (RadioButton) scene.lookup("#professorsDeleteRadio");

        professorIdField = (TextField) scene.lookup("#professorIdField");
        professorNameField = (TextField) scene.lookup("#professorNameField");
        professorPhoneNumField = (TextField) scene.lookup("#professorPhoneNumField");
        professorBirthdateField = (DatePicker) scene.lookup("#professorBirthdateField");
        professorAddressField = (TextField) scene.lookup("#professorAddressField");
        professorEmailField = (TextField) scene.lookup("#professorEmailField");
        professorSubjectsCombo = (CheckComboBox) scene.lookup("#professorSubjectsCombo");
    }
}
