package com.portal.repo;

import com.portal.hikari.DataSource;
import com.portal.model.Group;
import com.portal.model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {
    public SubjectRepository() {
    }

    public void createTable() throws SQLException {
        String SQL_QUERY = """
                DROP TABLE IF EXISTS "Subjects";
                CREATE TABLE "Subjects"
                (
                subject_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 1000000 CACHE 1 ),
                subject_name character varying(255) NOT NULL,
                description text,
                professor_id integer
                );""";

        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {
            pst.executeUpdate();
        }
    }

    public List<Subject> getSubjects() throws SQLException {
        String SQL_QUERY = "SELECT * FROM \"Subjects\"";
        List<Subject> subjectList = new ArrayList<>();
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery();) {
            Subject subject;
            while (rs.next()) {
                subject = new Subject(
                        rs.getInt("subject_id"),
                        rs.getString("subject_name"),
                        rs.getString("description"),
                        rs.getInt("professor_id")
                );
                subjectList.add(subject);
            }
        }
        return subjectList;
    }

    public boolean createSubject(Subject subject) throws SQLException {
        String SQL_QUERY = "INSERT INTO \"Subjects\" (subject_name, description, professor_id) VALUES (?, ?, ?)";
        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setString(1, subject.getSubjectName());
            pst.setString(2, subject.getDescription());
            pst.setInt(3, subject.getProfessorId());

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean updateSubject(Subject subject) throws SQLException {
        String SQL_QUERY = """
                UPDATE "Subjects"
                SET subject_name = ?, description = ?, professor_id = ?
                WHERE subject_id = ?;""";
        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setString(1, subject.getSubjectName());
            pst.setString(2, subject.getDescription());
            pst.setInt(3, subject.getProfessorId());
            pst.setInt(4, subject.getSubjectId());

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean deleteSubject(Subject subject) throws SQLException {
        String SQL_QUERY = """
                DELETE FROM "Subjects"
                WHERE subject_id = ?;""";

        int rows = 0;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, subject.getSubjectId());
            rows = pst.executeUpdate();
        }
        return rows > 0;
    }
}
