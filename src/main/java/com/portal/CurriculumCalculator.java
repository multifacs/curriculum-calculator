package com.portal;

import com.portal.model.Curriculum;
import com.portal.model.Group;
import com.portal.model.SemesterHoursData;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CurriculumCalculator {

    private Workbook workbook;
    private Sheet sheet;
    private List<String> subjects;
    private List<SemesterHoursData> semesterHoursDataList;

    public List<String> getSubjects() {
        return subjects;
    }

    public List<SemesterHoursData> getSemesterHoursDataList() {
        return semesterHoursDataList;
    }

    public CurriculumCalculator() {
    }

    public CurriculumCalculator(File file) {
        try {
            workbook = new XSSFWorkbook(file);
            this.sheet = workbook.getSheet("План");
            System.out.println(sheet.getSheetName());
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        calculate();
    }

    public String getDirection() {
        var titleSheet = workbook.getSheet("Титул");
        var dirName = titleSheet.getRow(28).getCell(3).getStringCellValue();
        System.out.println("dirName = " + dirName.substring(12));
        return dirName.substring(12);
    }

    public void calculate() {

        subjects = new ArrayList<>();
        semesterHoursDataList = new ArrayList<>();

        int subjectCounter = 0;
        for (Row row : sheet) {
            Cell firstCell = row.getCell(1);
//            System.out.println("firstCell.getStringCellValue().length() = " + firstCell.getStringCellValue().length());
            if (firstCell.getStringCellValue().length() == 7) {
                Cell subject = row.getCell(2);
                System.out.print(subject.getStringCellValue());

                subjects.add(subject.getStringCellValue());

                subjectCounter++;

                String sem1Lectures = row.getCell(16).getStringCellValue();
                String sem1Practice = row.getCell(15).getStringCellValue();

                if (sem1Lectures.length() > 0) {
                    System.out.print(" sem1: " + sem1Practice + " " + sem1Lectures);

                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 1;
                    semesterHoursData.lectureHours = Integer.parseInt(sem1Lectures);
                    semesterHoursData.practiceHours = Integer.parseInt(sem1Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);

                }

                String sem2Lectures = row.getCell(26).getStringCellValue();
                String sem2Practice = row.getCell(25).getStringCellValue();

                if (sem2Lectures.length() > 0) {
                    System.out.print(" sem2: " + sem2Practice + " " + sem2Lectures);

                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 2;
                    semesterHoursData.lectureHours = Integer.parseInt(sem2Lectures);
                    semesterHoursData.practiceHours = Integer.parseInt(sem2Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);
                }

                String sem3Lectures = row.getCell(36).getStringCellValue();
                String sem3Practice = row.getCell(35).getStringCellValue();

                if (sem3Lectures.length() > 0) {
                    System.out.print(" sem3: " + sem3Practice + " " + sem3Lectures);
                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 3;
                    semesterHoursData.lectureHours = Integer.parseInt(sem3Lectures);
                    semesterHoursData.practiceHours = Integer.parseInt(sem3Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);
                }

                String sem4Lectures = row.getCell(46).getStringCellValue();
                String sem4Practice = row.getCell(45).getStringCellValue();

                if (sem4Lectures.length() > 0) {
                    System.out.print(" sem4: '" + sem4Practice + "' '" + sem4Lectures + "'");

                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 4;
                    int lectures = 0;
                    try {
                        lectures = Integer.parseInt(sem4Lectures);
                    } catch (NumberFormatException e) {
                        System.out.println("wrong formal");
                    }
                    semesterHoursData.lectureHours = lectures;
                    semesterHoursData.practiceHours = Integer.parseInt(sem4Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);
                }

                String sem5Lectures = row.getCell(56).getStringCellValue();
                String sem5Practice = row.getCell(55).getStringCellValue();

                if (sem5Lectures.length() > 0) {
                    System.out.print(" sem5: " + sem5Practice + " " + sem5Lectures);

                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 5;
                    semesterHoursData.lectureHours = Integer.parseInt(sem5Lectures);
                    semesterHoursData.practiceHours = Integer.parseInt(sem5Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);
                }

                String sem6Lectures = row.getCell(66).getStringCellValue();
                String sem6Practice = row.getCell(65).getStringCellValue();

                if (sem6Lectures.length() > 0) {
                    System.out.print(" sem6: " + sem6Practice + " " + sem6Lectures);

                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 6;
                    semesterHoursData.lectureHours = Integer.parseInt(sem6Lectures);
                    semesterHoursData.practiceHours = Integer.parseInt(sem6Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);
                }

                String sem7Lectures = row.getCell(76).getStringCellValue();
                String sem7Practice = row.getCell(75).getStringCellValue();

                if (sem7Lectures.length() > 0) {
                    System.out.print(" sem7: " + sem7Practice + " " + sem7Lectures);

                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 7;
                    semesterHoursData.lectureHours = Integer.parseInt(sem7Lectures);
                    semesterHoursData.practiceHours = Integer.parseInt(sem7Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);
                }

                String sem8Lectures = row.getCell(86).getStringCellValue();
                String sem8Practice = row.getCell(85).getStringCellValue();

                if (sem8Lectures.length() > 0) {
                    System.out.print(" sem8: " + sem8Practice + " " + sem8Lectures);

                    SemesterHoursData semesterHoursData = new SemesterHoursData();
                    semesterHoursData.subject = subject.getStringCellValue();
                    semesterHoursData.semester = 8;
                    semesterHoursData.lectureHours = Integer.parseInt(sem8Lectures);
                    semesterHoursData.practiceHours = Integer.parseInt(sem8Practice) - semesterHoursData.lectureHours;

                    semesterHoursDataList.add(semesterHoursData);
                }
                System.out.println();
            }
        }
        System.out.println("subjectCounter = " + subjectCounter);
    }

    public void initialize(Scene scene, DBConnection dbConnection) {
        Label dirLabel = (Label) scene.lookup("#directionLabel");
        dirLabel.setText(getDirection());


//        TableView<String> loadedSubjectsTable = (TableView) scene.lookup("#loadedSubjectsTable");
//
//        TableColumn<String, String> column1 =
//                new TableColumn<>("Предмет");
//        column1.setCellValueFactory(
//                data -> new SimpleStringProperty(data.getValue()));
//
//        loadedSubjectsTable.getColumns().clear();
//        loadedSubjectsTable.getColumns().addAll(column1);
//
//        loadedSubjectsTable.getItems().clear();
//        loadedSubjectsTable.getItems().addAll(subjects);


        TableView<SemesterHoursData> loadedHoursTable = (TableView) scene.lookup("#loadedHoursTable");

        TableColumn<SemesterHoursData, String> column2 =
                new TableColumn<>("Предмет");
        column2.setCellValueFactory(
                new PropertyValueFactory<>("subject"));

        TableColumn<SemesterHoursData, Integer> column3 =
                new TableColumn<>("Семестр");
        column3.setCellValueFactory(
                new PropertyValueFactory<>("semester"));

        TableColumn<SemesterHoursData, Integer> column4 =
                new TableColumn<>("Лекции");
        column4.setCellValueFactory(
                new PropertyValueFactory<>("lectureHours"));

        TableColumn<SemesterHoursData, Integer> column5 =
                new TableColumn<>("Практики");
        column5.setCellValueFactory(
                new PropertyValueFactory<>("practiceHours"));

        loadedHoursTable.getColumns().clear();
        loadedHoursTable.getColumns().addAll(column2, column3, column4, column5);

        loadedHoursTable.getItems().clear();
        loadedHoursTable.getItems().addAll(semesterHoursDataList);
    }

}

