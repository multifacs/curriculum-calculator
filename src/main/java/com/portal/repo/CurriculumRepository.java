package com.portal.repo;

import com.portal.hikari.DataSource;
import com.portal.model.Curriculum;
import com.portal.model.Group;
import com.portal.model.Professor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CurriculumRepository {

    public CurriculumRepository() {
    }

    public void createTable() throws SQLException {
        String SQL_QUERY = """
                DROP TABLE IF EXISTS "Curricula";
                CREATE TABLE "Curricula"
                (
                curriculum_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 1000000 CACHE 1 ),
                subject_id integer,
                lecture_hours integer,
                practice_hours integer,
                semester integer,
                direction character varying
                );
                """;

        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {
            pst.executeUpdate();
        }
    }

    public List<Curriculum> getCurricula() throws SQLException {
        String SQL_QUERY = "SELECT * FROM \"Curricula\"";
        List<Curriculum> curriculumList = new ArrayList<>();
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery();) {
            Curriculum curriculum;
            while (rs.next()) {
                curriculum = new Curriculum(
                        rs.getInt("curriculum_id"),
                        rs.getInt("lecture_hours"),
                        rs.getInt("practice_hours"),
                        rs.getString("direction"),
                        rs.getInt("subject_id"),
                        rs.getInt("semester")
                );
                curriculumList.add(curriculum);
            }
        }
        return curriculumList;
    }

    public boolean createCurriculum(Curriculum curriculum) throws SQLException {
        String SQL_QUERY = """
                INSERT INTO "Curricula"
                (lecture_hours, practice_hours, direction, subject_id, semester)
                VALUES
                (?, ?, ?, ?, ?)
                """;
        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, curriculum.getLectureHours());
            pst.setInt(2, curriculum.getPracticeHours());
            pst.setString(3, curriculum.getDirection());
            pst.setInt(4, curriculum.getSubjectId());
            pst.setInt(5, curriculum.getSemester());

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean updateCurriculum(Curriculum curriculum) throws SQLException {
        String SQL_QUERY = """
                UPDATE "Curricula"
                SET lecture_hours = ?, practice_hours = ?, direction = ?, subject_id = ?, semester = ?
                WHERE curriculum_id = ?;
                """;
        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, curriculum.getLectureHours());
            pst.setInt(2, curriculum.getPracticeHours());
            pst.setString(3, curriculum.getDirection());
            pst.setInt(4, curriculum.getSubjectId());
            pst.setInt(5, curriculum.getSemester());
            pst.setInt(6, curriculum.getCurriculumId());

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean deleteCurriculum(Curriculum curriculum) throws SQLException {
        String SQL_QUERY = """
                DELETE FROM "Curricula"
                WHERE curriculum_id = ?;""";

        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, curriculum.getCurriculumId());
            rows = pst.executeUpdate();
        }
        return rows > 0;
    }
}
