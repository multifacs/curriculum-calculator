package com.portal;

import com.portal.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class PortalClient extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PortalClient.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setTitle("Портал");
        stage.setScene(scene);
        stage.show();

        DBConnection dbConnection = new DBConnection();
        Label loadingLabel = (Label) scene.lookup("#loadingText");
        ProgressIndicator progressIndicator = (ProgressIndicator) scene.lookup("#progressIndicator");
        StatusObserver statusObserver = new StatusObserver(dbConnection, loadingLabel, progressIndicator);
        dbConnection.connect();

        Label loginLabel = (Label) scene.lookup("#loginLabel");
        TextField usernameField = (TextField) scene.lookup("#usernameField");
        PasswordField passwordField = (PasswordField) scene.lookup("#passwordField");
        Button loginBtn = (Button) scene.lookup("#loginBtn");

        Label userLabel = (Label) scene.lookup("#userLabel");
        Button logoutBtn = (Button) scene.lookup("#logoutBtn");

        disableTabs(scene, true);

        setGroupsListeners(scene, dbConnection);
        setProfessorsListeners(scene, dbConnection);
        setSubjectsListeners(scene, dbConnection);
        setCurriculaListeners(scene, dbConnection);

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            Platform.runLater(() -> {
                if (dbConnection.login(username, password)) {
                    loginLabel.setText("Успешно");

                    userLabel.setText(username);
                    loginBtn.setDisable(true);
                    logoutBtn.setDisable(false);
                    usernameField.setDisable(true);
                    passwordField.setDisable(true);

                    disableTabs(scene, false);
                    initializeGroups(scene, dbConnection);
                    initializeProfessors(scene, dbConnection);
                    initializeSubjects(scene, dbConnection);
                    initializeCurricula(scene, dbConnection);

                    initializeLoadingTab(scene, stage, dbConnection);
                    initializeDistributionTab(stage, scene, dbConnection);
                } else {
                    loginLabel.setText("Ошибка");

                    userLabel.setText("none");
                    loginBtn.setDisable(false);
                    logoutBtn.setDisable(true);
                    usernameField.setDisable(false);
                    passwordField.setDisable(false);

                    disableTabs(scene, true);
                }
            });
        });
        logoutBtn.setOnAction(e -> {
            dbConnection.logout();
            userLabel.setText("none");
            loginBtn.setDisable(false);
            logoutBtn.setDisable(true);
            usernameField.setDisable(false);
            passwordField.setDisable(false);

            disableTabs(scene, true);
        });
        boolean autoLogin = true;
        boolean migrate = false;
        if (autoLogin) {
            Platform.runLater(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                dbConnection.login("test_user", "123");
                loginLabel.setText("Успешно");

                if (migrate) {
                    dbConnection.migrate();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                userLabel.setText("test_user");
                loginBtn.setDisable(true);
                logoutBtn.setDisable(false);

                disableTabs(scene, false);
                initializeGroups(scene, dbConnection);
                initializeProfessors(scene, dbConnection);
                initializeSubjects(scene, dbConnection);
                initializeCurricula(scene, dbConnection);

                initializeLoadingTab(scene, stage, dbConnection);
                initializeDistributionTab(stage, scene, dbConnection);

                Button distributionUpdBtn = (Button) scene.lookup("#distributionUpdBtn");
                distributionUpdBtn.setOnAction(e -> {
                    initializeDistributionTab(stage, scene, dbConnection);
                });
            });
        }
    }

    private void disableTabs(Scene scene, boolean value) {
        Node tabProfessors = scene.lookup("#tabProfessors");
        tabProfessors.setDisable(value);

        Node tabSubjects = scene.lookup("#tabSubjects");
        tabSubjects.setDisable(value);

        Node tabGroups = scene.lookup("#tabGroups");
        tabGroups.setDisable(value);

        Node tabCurrs = scene.lookup("#tabCurriculums");
        tabCurrs.setDisable(value);

        Node tabLoad = scene.lookup("#tabLoad");
        tabLoad.setDisable(value);

        Node tabDistribution = scene.lookup("#tabDistribution");
        tabDistribution.setDisable(value);
    }

    private void setGroupsListeners(Scene scene, DBConnection dbConnection) {
        TableView groupsTable = (TableView) scene.lookup("#groupsTable");

        RadioButton groupsAddRadio = (RadioButton) scene.lookup("#groupsAddRadio");
        RadioButton groupsEditRadio = (RadioButton) scene.lookup("#groupsEditRadio");
        RadioButton groupsDeleteRadio = (RadioButton) scene.lookup("#groupsDeleteRadio");
        TextField groupIdField = (TextField) scene.lookup("#groupIdField");
        TextField numOfGroupField = (TextField) scene.lookup("#numOfGroupField");
        TextField numOfStudentsField = (TextField) scene.lookup("#numOfStudentsField");
        TextField dirField = (TextField) scene.lookup("#dirField");
        TextField groupPhoneField = (TextField) scene.lookup("#groupPhoneField");

        groupsTable.setOnMouseClicked(e -> {
            Group group = (Group) groupsTable.getFocusModel().getFocusedItem();

            Platform.runLater(() -> {
                if (!groupsAddRadio.isSelected()) {
                    groupIdField.setText(String.valueOf(group.getGroupId()));
                    numOfGroupField.setText(String.valueOf(group.getGroupNum()));
                    numOfStudentsField.setText(String.valueOf(group.getStudentsNum()));
                    dirField.setText(String.valueOf(group.getDirection()));
                    groupPhoneField.setText(String.valueOf(group.getPhoneNum()));
                }
            });
        });

        Button groupActionBtn = (Button) scene.lookup("#groupActionBtn");
        groupActionBtn.setOnAction(e -> {
            int id = 0;
            String groupNum = null;
            int studentsNum = 0;
            String direction = null;
            String phoneNum = null;

            if (groupsAddRadio.isSelected() || groupsEditRadio.isSelected()) {
                try {
                    groupNum = numOfGroupField.getText();
                    if (numOfStudentsField.getText().length() != 0) {
                        studentsNum = Integer.parseInt(numOfStudentsField.getText());
                    }
                    direction = dirField.getText();
                    phoneNum = groupPhoneField.getText();
                    assert direction.length() > 0;
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                    return;
                }
            }
            if (groupsEditRadio.isSelected() || groupsDeleteRadio.isSelected()) {
                try {
                    id = Integer.parseInt(groupIdField.getText());
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                    return;
                }
            }

            if (groupsAddRadio.isSelected()) {
                try {
                    dbConnection.addGroup(new Group(0, groupNum, studentsNum, direction, phoneNum));
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            } else if (groupsEditRadio.isSelected()) {
                try {
                    dbConnection.updateGroup(new Group(id, groupNum, studentsNum, direction, phoneNum));
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            } else {
                try {
                    dbConnection.deleteGroup(id);
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            }
            updateGroups(scene, dbConnection);
        });

        Button groupsUpdateBtn = (Button) scene.lookup("#groupsUpdateBtn");
        groupsUpdateBtn.setOnAction(e -> {
            updateGroups(scene, dbConnection);
        });
    }

    public void initializeGroups(Scene scene, DBConnection dbConnection) {
        TableView groupsTable = (TableView) scene.lookup("#groupsTable");

        TableColumn<Group, String> column1 =
                new TableColumn<>("ID");
        column1.setCellValueFactory(
                new PropertyValueFactory<>("groupId"));

        TableColumn<Group, String> column2 =
                new TableColumn<>("№ группы");
        column2.setCellValueFactory(
                new PropertyValueFactory<>("groupNum"));

        TableColumn<Group, String> column3 =
                new TableColumn<>("Число студентов");
        column3.setCellValueFactory(
                new PropertyValueFactory<>("studentsNum"));

        TableColumn<Group, String> column4 =
                new TableColumn<>("Направление");
        column4.setCellValueFactory(
                new PropertyValueFactory<>("direction"));

        TableColumn<Group, String> column5 =
                new TableColumn<>("Тел. номер");
        column5.setCellValueFactory(
                new PropertyValueFactory<>("phoneNum"));

        groupsTable.getColumns().clear();
        groupsTable.getColumns().addAll(column1, column2, column3, column4, column5);

        AnchorPane anchorPane = (AnchorPane) scene.lookup("#groupsAnchorPane");
        anchorPane.prefWidthProperty().bind(scene.widthProperty());
        VBox vBox = (VBox) scene.lookup("#groupsVBox");
        vBox.prefWidthProperty().bind(scene.widthProperty());

        updateGroups(scene, dbConnection);
    }

    private void updateGroups(Scene scene, DBConnection dbConnection) {
        TableView groupsTable = (TableView) scene.lookup("#groupsTable");

        List<Group> groupList = dbConnection.getGroups();
        groupsTable.getItems().clear();
        groupsTable.getItems().addAll(groupList);

        Label numOfGroupsLabel = (Label) scene.lookup("#numOfGroupsLabel");
        numOfGroupsLabel.setText(String.valueOf(groupList.size()));
    }


    private void setProfessorsListeners(Scene scene, DBConnection dbConnection) {
        TableView professorsTable = (TableView) scene.lookup("#professorsTable");

        RadioButton professorsAddRadio = (RadioButton) scene.lookup("#professorsAddRadio");
        RadioButton professorsEditRadio = (RadioButton) scene.lookup("#professorsEditRadio");
        RadioButton professorsDeleteRadio = (RadioButton) scene.lookup("#professorsDeleteRadio");

        TextField professorIdField = (TextField) scene.lookup("#professorIdField");
        TextField professorNameField = (TextField) scene.lookup("#professorNameField");
        TextField professorPhoneNumField = (TextField) scene.lookup("#professorPhoneNumField");
        DatePicker professorBirthdateField = (DatePicker) scene.lookup("#professorBirthdateField");
        TextField professorAddressField = (TextField) scene.lookup("#professorAddressField");
        TextField professorEmailField = (TextField) scene.lookup("#professorEmailField");
        CheckComboBox professorSubjectsCombo = (CheckComboBox) scene.lookup("#professorSubjectsCombo");

        professorsTable.setOnMouseClicked(e -> {
            Professor professor = (Professor) professorsTable.getFocusModel().getFocusedItem();

            Platform.runLater(() -> {
                if (!professorsAddRadio.isSelected()) {
                    professorIdField.setText(String.valueOf(professor.getProfessorId()));
                    professorNameField.setText(String.valueOf(professor.getFullName()));
                    professorPhoneNumField.setText(String.valueOf(professor.getPhoneNum()));
                    professorBirthdateField.setValue(professor.getBirthDate());
                    professorAddressField.setText(String.valueOf(professor.getAddress()));
                    professorEmailField.setText(String.valueOf(professor.getEmail()));
                    IndexedCheckModel<Subject> checkModel = professorSubjectsCombo.getCheckModel();
                    professor.getSubjects().forEach(s -> {
                        int index = checkModel
                                .getItemIndex(
                                        dbConnection
                                                .getLoadedSubjects()
                                                .stream()
                                                .filter(sub -> Objects
                                                        .equals(sub.getSubjectId(), s))
                                                .findFirst()
                                                .orElse(null)
                                );
                        checkModel.check(index);
                    });
//                    checkModel.getCheckedIndices().addAll(professor.getSubjects());
                    professorSubjectsCombo.setCheckModel(checkModel);
                }
            });
        });

        Button professorsActionBtn = (Button) scene.lookup("#professorsActionBtn");
        professorsActionBtn.setOnAction(e -> {
            int id = 0;
            String fullName = null;
            String phoneNum = null;
            LocalDate birthdate = null;
            String address = null;
            String email = null;
            List<Integer> subjects = new ArrayList<>();

            if (professorsAddRadio.isSelected() || professorsEditRadio.isSelected()) {
                try {
                    fullName = professorNameField.getText();
                    System.out.println("fullName = " + fullName.length());
                    assert fullName.length() > 0;
                    phoneNum = professorPhoneNumField.getText();
                    assert phoneNum.length() > 0;
                    birthdate = professorBirthdateField.getValue();
                    address = professorAddressField.getText();
                    email = professorEmailField.getText();
                    for (var i : professorSubjectsCombo.getCheckModel().getCheckedItems()) {
                        subjects.add(((Subject) i).getSubjectId());
                    }
                    System.out.println("subjects = " + subjects);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                    return;
                }
            }
            if (professorsEditRadio.isSelected() || professorsDeleteRadio.isSelected()) {
                try {
                    id = Integer.parseInt(professorIdField.getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                    return;
                }
            }

            if (professorsAddRadio.isSelected()) {
                try {
                    dbConnection.addProfessor(new Professor(0, fullName, phoneNum, birthdate, address, email, subjects));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                }
            } else if (professorsEditRadio.isSelected()) {
                try {
                    dbConnection.updateProfessor(new Professor(id, fullName, phoneNum, birthdate, address, email, subjects));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                }
            } else {
                try {
                    dbConnection.deleteProfessor(id);
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            }
            updateProfessors(scene, dbConnection);
        });

        Button professorsUpdateBtn = (Button) scene.lookup("#professorsUpdateBtn");
        professorsUpdateBtn.setOnAction(e -> {
            updateProfessors(scene, dbConnection);
        });
    }

    public void initializeProfessors(Scene scene, DBConnection dbConnection) {
        TableView professorsTable = (TableView) scene.lookup("#professorsTable");

        TableColumn<Professor, String> column1 =
                new TableColumn<>("ID");
        column1.setCellValueFactory(
                new PropertyValueFactory<>("professorId"));

        TableColumn<Professor, String> column2 =
                new TableColumn<>("ФИО");
        column2.setCellValueFactory(
                new PropertyValueFactory<>("fullName"));

        TableColumn<Professor, String> column3 =
                new TableColumn<>("Тел. номер");
        column3.setCellValueFactory(
                new PropertyValueFactory<>("phoneNum"));

        TableColumn<Professor, String> column4 =
                new TableColumn<>("Дата рождения");
        column4.setCellValueFactory(
                new PropertyValueFactory<>("birthDate"));

        TableColumn<Professor, String> column5 =
                new TableColumn<>("Адрес");
        column5.setCellValueFactory(
                new PropertyValueFactory<>("address"));

        TableColumn<Professor, String> column6 =
                new TableColumn<>("Email");
        column6.setCellValueFactory(
                new PropertyValueFactory<>("email"));

        TableColumn<Professor, String> column7 =
                new TableColumn<>("Предметы");
//        column7.setCellValueFactory(
//                new PropertyValueFactory<>("subjects"));

        column7.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                dbConnection.getLoadedSubjects().stream().filter(
                                        s -> data.getValue().getSubjects().contains(s.getSubjectId())
                                ).toList().toString()
                        )
        );

        professorsTable.getColumns().clear();
        professorsTable.getColumns().addAll(column1, column2, column3, column4, column5, column6, column7);

        updateProfessors(scene, dbConnection);

        AnchorPane anchorPane = (AnchorPane) scene.lookup("#professorsAnchorPane");
        anchorPane.prefWidthProperty().bind(scene.widthProperty());
        VBox vBox = (VBox) scene.lookup("#professorsVBox");
        vBox.prefWidthProperty().bind(scene.widthProperty());
    }

    private void updateProfessors(Scene scene, DBConnection dbConnection) {
        TableView professorsTable = (TableView) scene.lookup("#professorsTable");

        List<Professor> professorList = dbConnection.getProfessors();
        professorsTable.getItems().clear();
        professorsTable.getItems().addAll(professorList);

        CheckComboBox professorSubjectsCombo = (CheckComboBox) scene.lookup("#professorSubjectsCombo");
//        List<String> subjectList = dbConnection.getSubjects().stream().map(Subject::getSubjectName).toList();
        dbConnection.setSubjects();
//        List<Integer> subjectIDs = subjectList.stream().map(Subject::getSubjectId).toList();
        professorSubjectsCombo.getItems().clear();
//        professorSubjectsCombo.getItems().addAll(subjectIDs);
        professorSubjectsCombo.getItems().addAll(dbConnection.getLoadedSubjects());

        Label numOfProfessorsLabel = (Label) scene.lookup("#numOfProfessorsLabel");
        numOfProfessorsLabel.setText(String.valueOf(professorList.size()));
    }


    private void setSubjectsListeners(Scene scene, DBConnection dbConnection) {
        TableView subjectsTable = (TableView) scene.lookup("#subjectsTable");

        RadioButton subjectsAddRadio = (RadioButton) scene.lookup("#subjectsAddRadio");
        RadioButton subjectsEditRadio = (RadioButton) scene.lookup("#subjectsEditRadio");
        RadioButton subjectsDeleteRadio = (RadioButton) scene.lookup("#subjectsDeleteRadio");

        TextField subjectIdField = (TextField) scene.lookup("#subjectIdField");
        TextField subjectNameField = (TextField) scene.lookup("#subjectNameField");
        TextField descField = (TextField) scene.lookup("#descField");

        subjectsTable.setOnMouseClicked(e -> {
            Subject subject = (Subject) subjectsTable.getFocusModel().getFocusedItem();

            Platform.runLater(() -> {
                if (!subjectsAddRadio.isSelected()) {
                    subjectIdField.setText(String.valueOf(subject.getSubjectId()));
                    subjectNameField.setText(String.valueOf(subject.getSubjectName()));
                    descField.setText(String.valueOf(subject.getDescription()));
                }
            });
        });

        Button subjectsActionBtn = (Button) scene.lookup("#subjectsActionBtn");
        subjectsActionBtn.setOnAction(e -> {
            int id = 0;
            String name = null;
            String description = "";

            if (subjectsAddRadio.isSelected() || subjectsEditRadio.isSelected()) {
                try {
                    name = subjectNameField.getText();
                    assert name.length() > 0;
                    description = descField.getText();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                    return;
                }
            }
            if (subjectsEditRadio.isSelected() || subjectsDeleteRadio.isSelected()) {
                try {
                    id = Integer.parseInt(subjectIdField.getText());
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                    return;
                }
            }

            if (subjectsAddRadio.isSelected()) {
                try {
                    dbConnection.addSubject(new Subject(0, name, description));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                }
            } else if (subjectsEditRadio.isSelected()) {
                try {
                    dbConnection.updateSubject(new Subject(id, name, description));
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            } else {
                try {
                    dbConnection.deleteSubject(id);
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            }
            updateSubjects(scene, dbConnection);
        });

        Button subjectsUpdateBtn = (Button) scene.lookup("#subjectsUpdateBtn");
        subjectsUpdateBtn.setOnAction(e -> {
            updateSubjects(scene, dbConnection);
        });
    }

    public void initializeSubjects(Scene scene, DBConnection dbConnection) {
        TableView subjectsTable = (TableView) scene.lookup("#subjectsTable");

        TableColumn<Subject, String> column1 =
                new TableColumn<>("ID");
        column1.setCellValueFactory(
                new PropertyValueFactory<>("subjectId"));

        TableColumn<Subject, String> column2 =
                new TableColumn<>("Название");
        column2.setCellValueFactory(
                new PropertyValueFactory<>("subjectName"));

        TableColumn<Subject, String> column3 =
                new TableColumn<>("Описание");
        column3.setCellValueFactory(
                new PropertyValueFactory<>("description"));

        subjectsTable.getColumns().clear();
        subjectsTable.getColumns().addAll(column1, column2, column3);

        AnchorPane anchorPane = (AnchorPane) scene.lookup("#subjectsAnchorPane");
        anchorPane.prefWidthProperty().bind(scene.widthProperty());
        VBox vBox = (VBox) scene.lookup("#subjectsVBox");
        vBox.prefWidthProperty().bind(scene.widthProperty());

        updateSubjects(scene, dbConnection);
    }

    private void updateSubjects(Scene scene, DBConnection dbConnection) {
        TableView subjectsTable = (TableView) scene.lookup("#subjectsTable");

        List<Subject> subjectList = dbConnection.getSubjects();
        subjectsTable.getItems().clear();
        subjectsTable.getItems().addAll(subjectList);

        Label numOfSubjectsLabel = (Label) scene.lookup("#numOfSubjectsLabel");
        numOfSubjectsLabel.setText(String.valueOf(subjectList.size()));
    }


    private void setCurriculaListeners(Scene scene, DBConnection dbConnection) {
        TableView curriculaTable = (TableView) scene.lookup("#curriculaTable");

        RadioButton curriculaAddRadio = (RadioButton) scene.lookup("#curriculaAddRadio");
        RadioButton curriculaEditRadio = (RadioButton) scene.lookup("#curriculaEditRadio");
        RadioButton curriculaDeleteRadio = (RadioButton) scene.lookup("#curriculaDeleteRadio");

        TextField curriculumIdField = (TextField) scene.lookup("#curriculumIdField");
        TextField curriculumLecturesHoursField = (TextField) scene.lookup("#curriculumLecturesHoursField");
        TextField curriculumPracticeHoursField = (TextField) scene.lookup("#curriculumPracticeHoursField");
        TextField curriculumDirectionField = (TextField) scene.lookup("#curriculumDirectionField");
        ChoiceBox<Subject> curriculumSubjectId = (ChoiceBox<Subject>) scene.lookup("#curriculumSubjectId");
        TextField curriculumSemesterField = (TextField) scene.lookup("#curriculumSemesterField");

        curriculaTable.setOnMouseClicked(e -> {
            Curriculum curriculum = (Curriculum) curriculaTable.getFocusModel().getFocusedItem();

            Platform.runLater(() -> {
                if (!curriculaAddRadio.isSelected()) {
                    curriculumIdField.setText(String.valueOf(curriculum.getCurriculumId()));
                    curriculumLecturesHoursField.setText(String.valueOf(curriculum.getLectureHours()));
                    curriculumPracticeHoursField.setText(String.valueOf(curriculum.getPracticeHours()));
                    curriculumDirectionField.setText(curriculum.getDirection());
                    curriculumSubjectId.setValue(dbConnection
                            .getLoadedSubjects()
                            .stream()
                            .filter(s -> Objects.equals(s.getSubjectId(), curriculum.getSubjectId())).findFirst().orElse(new Subject()));
                    curriculumSemesterField.setText(String.valueOf(curriculum.getSemester()));
                }
            });
        });

        Button curriculumActionBtn = (Button) scene.lookup("#curriculumActionBtn");
        curriculumActionBtn.setOnAction(e -> {
            int id = 0;
            int lectureHours = 0;
            int practiceHours = 0;
            String direction = "";
            int subjectId = 0;
            int semester = 0;

            if (curriculaAddRadio.isSelected() || curriculaEditRadio.isSelected()) {
                try {
                    lectureHours = Integer.parseInt(curriculumLecturesHoursField.getText());
                    practiceHours = Integer.parseInt(curriculumPracticeHoursField.getText());
                    direction = curriculumDirectionField.getText();
                    subjectId = curriculumSubjectId.getValue().getSubjectId();
                    semester = Integer.parseInt(curriculumSemesterField.getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                    return;
                }
            }
            if (curriculaEditRadio.isSelected() || curriculaDeleteRadio.isSelected()) {
                try {
                    id = Integer.parseInt(curriculumIdField.getText());
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                    return;
                }
            }

            if (curriculaAddRadio.isSelected()) {
                try {
                    dbConnection.addCurriculum(new Curriculum(0, lectureHours, practiceHours, direction, subjectId, semester));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Wrong format");
                }
            } else if (curriculaEditRadio.isSelected()) {
                try {
                    dbConnection.updateCurriculum(new Curriculum(id, lectureHours, practiceHours, direction, subjectId, semester));
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            } else {
                try {
                    dbConnection.deleteSubject(id);
                } catch (Exception ex) {
                    System.out.println("Wrong format");
                }
            }
            updateCurricula(scene, dbConnection);
        });

        Button curriculumsUpdateBtn = (Button) scene.lookup("#curriculumsUpdateBtn");
        curriculumsUpdateBtn.setOnAction(e -> {
            updateCurricula(scene, dbConnection);
        });
    }

    public void initializeCurricula(Scene scene, DBConnection dbConnection) {
        TableView curriculaTable = (TableView) scene.lookup("#curriculaTable");

        TableColumn<Curriculum, Integer> column1 =
                new TableColumn<>("ID");
        column1.setCellValueFactory(
                new PropertyValueFactory<>("curriculumId"));

        TableColumn<Curriculum, Integer> column2 =
                new TableColumn<>("Часы лекций");
        column2.setCellValueFactory(
                new PropertyValueFactory<>("lectureHours"));

        TableColumn<Curriculum, Integer> column3 =
                new TableColumn<>("Часы практик");
        column3.setCellValueFactory(
                new PropertyValueFactory<>("practiceHours"));

        TableColumn<Curriculum, String> column4 =
                new TableColumn<>("Направление");
        column4.setCellValueFactory(
                new PropertyValueFactory<>("direction"));

        TableColumn<Curriculum, String> column5 =
                new TableColumn<>("Предмет");
//        column5.setCellValueFactory(
//                new PropertyValueFactory<>("subjectId"));
        column5.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                dbConnection.getLoadedSubjects().stream().filter(
                                        s -> Objects.equals(data.getValue().getSubjectId(), s.getSubjectId())
                                ).findFirst().orElse(new Subject()).getSubjectName()
                        )
        );

        TableColumn<Curriculum, Integer> column6 =
                new TableColumn<>("Семестр");
        column6.setCellValueFactory(
                new PropertyValueFactory<>("semester"));

        curriculaTable.getColumns().clear();
        curriculaTable.getColumns().addAll(column1, column2, column3, column4, column5, column6);

        AnchorPane anchorPane = (AnchorPane) scene.lookup("#curriculaAnchorPane");
        anchorPane.prefWidthProperty().bind(scene.widthProperty());
        VBox vBox = (VBox) scene.lookup("#curriculaVBox");
        vBox.prefWidthProperty().bind(scene.widthProperty());

        updateCurricula(scene, dbConnection);
    }

    private void updateCurricula(Scene scene, DBConnection dbConnection) {
        TableView curriculaTable = (TableView) scene.lookup("#curriculaTable");

        List<Curriculum> curriculumList = dbConnection.getCurricula();
        curriculaTable.getItems().clear();
        curriculaTable.getItems().addAll(curriculumList);

        ChoiceBox<Subject> curriculumSubjectId = (ChoiceBox<Subject>) scene.lookup("#curriculumSubjectId");
//        List<Subject> subjectList = dbConnection.getSubjects();
        dbConnection.getSubjects();
//        List<Integer> subjectIDs = subjectList.stream().map(Subject::getSubjectId).toList();
        curriculumSubjectId.getItems().clear();
        curriculumSubjectId.getItems().addAll(dbConnection.getLoadedSubjects());

        Label numOfCurriculasLabel = (Label) scene.lookup("#numOfCurriculasLabel");
        numOfCurriculasLabel.setText(String.valueOf(curriculumList.size()));
    }

    public void initializeLoadingTab(Scene scene, Stage stage, DBConnection dbConnection) {

        AnchorPane loadingTabAnchorPane = (AnchorPane) scene.lookup("#loadingTabAnchorPane");
        loadingTabAnchorPane.prefWidthProperty().bind(scene.widthProperty());
        VBox loadedVBox = (VBox) scene.lookup("#loadedVBox");
        loadedVBox.prefWidthProperty().bind(scene.widthProperty());
        HBox loadedHBox = (HBox) scene.lookup("#loadedHBox");
        loadedHBox.prefWidthProperty().bind(scene.widthProperty());
//        VBox loadedSubjectsVBox = (VBox) scene.lookup("#loadedSubjectsVBox");
//        loadedSubjectsVBox.prefWidthProperty().bind(scene.widthProperty());
//        loadedSubjectsVBox.setMaxWidth(700);
        VBox loadedHoursVBox = (VBox) scene.lookup("#loadedHoursVBox");
        loadedHoursVBox.prefWidthProperty().bind(scene.widthProperty());

        // Open file func
        Button openFileBtn = (Button) scene.lookup("#openFileBtn");
        Button loadBtn = (Button) scene.lookup("#loadBtn");
        FileChooser fileChooser = new FileChooser();

        var ref = new Object() {
            List<String> subjectList;
            List<SemesterHoursData> semesterHoursDataList;
            String direction;
        };

        openFileBtn.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(stage);
            System.out.println("selectedFile = " + selectedFile);

            CurriculumCalculator curriculumCalculator = new CurriculumCalculator(selectedFile);
            curriculumCalculator.initialize(scene, dbConnection);

            ref.subjectList = curriculumCalculator.getSubjects();
            ref.semesterHoursDataList = curriculumCalculator.getSemesterHoursDataList();
            ref.direction = curriculumCalculator.getDirection();
        });

        loadBtn.setOnAction(e -> {
            List<Subject> subjects = dbConnection.getSubjects();
            List<String> subjectNames = subjects.stream().map(Subject::getSubjectName).toList();
            for (String s : ref.subjectList) {
                if (subjectNames.contains(s)) {
                    continue;
                }

                Subject newSubject = new Subject(0, s, "");
                dbConnection.addSubject(newSubject);
            }

            subjects = dbConnection.getSubjects();

            for (SemesterHoursData d : ref.semesterHoursDataList) {
                Curriculum newCurriculum = new Curriculum(
                        0,
                        d.lectureHours,
                        d.practiceHours,
                        ref.direction,
                        subjects.stream().filter(x -> Objects.equals(x.getSubjectName(), d.subject)).toList().get(0).getSubjectId(),
                        d.semester
                );

                dbConnection.addCurriculum(newCurriculum);
            }
        });

        Button clearSubjectsBtn = (Button) scene.lookup("#clearSubjectsBtn");
        clearSubjectsBtn.setOnAction(e -> {
            dbConnection.truncateSubjects();
        });
        Button clearCurriculaBtn = (Button) scene.lookup("#clearCurriculaBtn");
        clearCurriculaBtn.setOnAction(e -> {
            dbConnection.truncateCurricula();
        });
    }

    public void initializeDistributionTab(Stage stage, Scene scene, DBConnection dbConnection) {

        AnchorPane tabDistributionAnchorPane = (AnchorPane) scene.lookup("#tabDistributionAnchorPane");
        tabDistributionAnchorPane.prefWidthProperty().bind(scene.widthProperty());

        VBox tabDistributionVBox = (VBox) scene.lookup("#tabDistributionVBox");
        tabDistributionVBox.prefWidthProperty().bind(scene.widthProperty());

        dbConnection.setProfessors();
        dbConnection.setSubjects();

        ChoiceBox<Professor> distributionProfessorPicker = (ChoiceBox<Professor>) scene.lookup("#distributionProfessorPicker");
//        List<Integer> professorIDs = dbConnection.getLoadedProfessors().stream().map(Professor::getProfessorId).toList();
        distributionProfessorPicker.getItems().clear();
        distributionProfessorPicker.getItems().addAll(dbConnection.getLoadedProfessors());

        var ref = new Object() {
            Professor pickedProfessor = dbConnection.getLoadedProfessors().get(0);
            List<Curriculum> data = new ArrayList<>();
        };

        distributionProfessorPicker.setOnAction(e -> {
            ref.pickedProfessor = dbConnection.getLoadedProfessors()
                    .stream()
                    .filter(p -> Objects.equals(p.getProfessorId(), distributionProfessorPicker.getValue().getProfessorId()))
                    .toList()
                    .get(0);
            System.out.println("ref.pickedProfessor = " + ref.pickedProfessor.getSubjects());
        });

        Button distributionActionBtn = (Button) scene.lookup("#distributionActionBtn");

        distributionActionBtn.setOnAction(e -> {
            dbConnection.setProfessors();
            dbConnection.setSubjects();

            List<Integer> subjectIDs = ref.pickedProfessor.getSubjects();
            List<Subject> subjects = dbConnection.getLoadedSubjects()
                                .stream()
                                .filter(s -> subjectIDs.contains(s.getSubjectId()))
                                .toList();
            List<Curriculum> curricula = dbConnection
                    .getCurricula()
                    .stream()
                    .filter(c -> subjectIDs.contains(c.getSubjectId()))
                    .toList();

            List<Group> groups = dbConnection.getGroups();
            List<Curriculum> pickedCurricula = new ArrayList<>();

            curricula.forEach(c -> {
                List<Professor> professors = dbConnection
                        .getProfessors()
                        .stream()
                        .filter(p -> p.getSubjects().contains(c.getSubjectId()))
                        .toList();
                int numOfProfessors = professors.size();
                Professor picked = professors.stream().filter(
                        p -> Objects.equals(p.getFullName(), ref.pickedProfessor.getFullName())
                ).findFirst().get();
                int numOfCurrent = professors.indexOf(picked);
                int groupsPerProfessor = groups.size() / numOfProfessors;

                System.out.println("numOfCurrent = " + numOfCurrent);

                int startIndex = numOfCurrent * groupsPerProfessor;
                int endIndex = (numOfCurrent + 1) * groupsPerProfessor;

                for (int i = startIndex; i < endIndex; i++) {
                    Curriculum cсс = new Curriculum(
                            0,
                            c.getLectureHours() / numOfProfessors,
                            c.getPracticeHours() / numOfProfessors,
                            c.getDirection(),
                            c.getSubjectId(),
                            c.getSemester()
                    );
                    cсс.groupName = groups.get(i).getGroupNum();
                    pickedCurricula.add(cсс);
                    System.out.println("cсс.groupName = " + cсс.groupName);
                }
            });

            TableView<Curriculum> distributionTable = (TableView<Curriculum>) scene.lookup("#distributionTable");
            distributionTable.getItems().clear();
            distributionTable.getItems().addAll(pickedCurricula);

            ref.data = pickedCurricula;
        });

        List<Subject> subjects = dbConnection.getSubjects();
        TableView<Curriculum> distributionTable = (TableView<Curriculum>) scene.lookup("#distributionTable");

        TableColumn<Curriculum, String> column1 =
                new TableColumn<>("Преподаватель");
        column1.setCellValueFactory(
                data -> new SimpleStringProperty(
                        ref.pickedProfessor.getFullName()
                ));

        TableColumn<Curriculum, String> column2 =
                new TableColumn<>("Группа");
        column2.setCellValueFactory(
                new PropertyValueFactory<>("groupName"));

        TableColumn<Curriculum, String> column3 =
                new TableColumn<>("Предмет");
        column3.setCellValueFactory(
                data -> new SimpleStringProperty(
                        subjects.stream().filter(s -> {
//                            System.out.println("data = " + data);
                            return Objects.equals(s.getSubjectId(), data.getValue().getSubjectId());
                        }).toList().get(0).getSubjectName()
                ));

        TableColumn<Curriculum, String> column4 =
                new TableColumn<>("Направление");
        column4.setCellValueFactory(
                new PropertyValueFactory<>("direction"));

        TableColumn<Curriculum, Integer> column5 =
                new TableColumn<>("Семестр");
        column5.setCellValueFactory(
                new PropertyValueFactory<>("semester"));

        TableColumn<Curriculum, Integer> column6 =
                new TableColumn<>("Часы лекций");
        column6.setCellValueFactory(
                new PropertyValueFactory<>("lectureHours"));

        TableColumn<Curriculum, Integer> column7 =
                new TableColumn<>("Часы практик");
        column7.setCellValueFactory(
                new PropertyValueFactory<>("practiceHours"));

        distributionTable.getColumns().clear();
        distributionTable.getColumns().addAll(column1, column2, column3, column4, column5, column6, column7);

        Button downloadXLSBtn = (Button) scene.lookup("#downloadXLS");
        downloadXLSBtn.setOnAction(a -> {
            System.out.println("bruh");
            CurriculumCalculator.saveFile(stage, ref.pickedProfessor.getFullName(), ref.data, subjects);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
