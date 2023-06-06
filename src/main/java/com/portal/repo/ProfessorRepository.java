package com.portal.repo;

import com.portal.hikari.DataSource;
import com.portal.model.Group;
import com.portal.model.Professor;
import com.portal.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProfessorRepository {

    public ProfessorRepository() {
    }

    public void createTable() throws SQLException {
        String SQL_QUERY = """
                DROP TABLE IF EXISTS "Professors";
                CREATE TABLE "Professors"
                (
                full_name character varying(255) NOT NULL,
                birth_date date,
                address character varying(255),
                email character varying(255),
                professor_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 1000000 CACHE 1 ),
                phone_number character varying,
                subjects integer[]
                )""";

        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {
            pst.executeUpdate();
        }
    }

    public List<Professor> getProfessors() throws SQLException {
        String SQL_QUERY = "SELECT * FROM \"Professors\"";
        List<Professor> professorList = new ArrayList<>();
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery();) {
            Professor professor;
            while (rs.next()) {
                List<Integer> subjects;
                var objectArray = (Object[]) rs.getArray("subjects").getArray();
                subjects = Arrays.stream(objectArray).map(ob -> (Integer) ob).collect(Collectors.toList());

                professor = new Professor(
                        rs.getInt("professor_id"),
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getString("address"),
                        rs.getString("email"),
                        subjects
                );
                professorList.add(professor);
            }
        }
        return professorList;
    }

    public boolean createProfessor(Professor professor) throws SQLException {
        String SQL_QUERY = """
                INSERT INTO "Professors"
                (full_name, phone_number, birth_date, address, email, subjects)
                VALUES
                (?, ?, ?, ?, ?, ?);
                """;
        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setString(1, professor.getFullName());
            pst.setString(2, professor.getPhoneNum());
            pst.setDate(3, Date.valueOf(professor.getBirthDate()));
            pst.setString(4, professor.getAddress());
            pst.setString(5, professor.getEmail());
            Array array = con.createArrayOf("integer", professor.getSubjects().toArray());
            pst.setArray(6, array);

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean updateProfessor(Professor professor) throws SQLException {
        String SQL_QUERY = """
                UPDATE "Professors"
                SET full_name = ?, phone_number = ?, birth_date = ?, address = ?, email = ?, subjects = ?
                WHERE subject_id = ?;""";
        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            Array subjectsArray = con.createArrayOf("int", professor.getSubjects().toArray());
            pst.setString(1, professor.getFullName());
            pst.setString(2, professor.getPhoneNum());
            pst.setDate(3, Date.valueOf(professor.getBirthDate()));
            pst.setString(4, professor.getAddress());
            pst.setString(5, professor.getEmail());
            pst.setArray(6, subjectsArray);
            pst.setInt(7, professor.getProfessorId());

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean deleteProfessor(Professor professor) throws SQLException {
        String SQL_QUERY = """
                DELETE FROM "Professors"
                WHERE professor_id = ?;""";

        int rows;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, professor.getProfessorId());
            rows = pst.executeUpdate();
        }
        return rows > 0;
    }
}
