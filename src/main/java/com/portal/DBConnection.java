package com.portal;

import com.portal.model.Curriculum;
import com.portal.model.Group;
import com.portal.model.Professor;
import com.portal.model.Subject;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class DBConnection extends Observable {
    Connection connection;
    String jdbcUrl = "jdbc:postgresql://localhost:5432/portal";
    String username = "postgres";
    String password = "admin";

    Thread sqlThread;

    private String statusText = "";

    private String user = "";

    public void setStatusText(String value) {
        synchronized (this) {
            this.statusText = value;
        }
        setChanged();
        notifyObservers();
    }

    public synchronized String getStatusText() {
        return statusText;
    }

    public DBConnection() {

    }

    public void connect() {
        sqlThread = new Thread(() -> {
            try {
                setStatusText("Connecting...");
                connection = DriverManager.getConnection(jdbcUrl, username, password);
                System.out.println("SQL connected");
                setStatusText("Connected");
            } catch (SQLException e) {
                System.out.println("SQL connection error");
                setStatusText("Connection error");
                e.printStackTrace();
            }
        });
        sqlThread.setDaemon(true);
        sqlThread.start();
    }

    public boolean login(String username, String password) {
        String sql = """
                SELECT * FROM "Users" WHERE username = ? AND password = ?
                """;

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            System.out.println(preparedStatement);

            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println("Login failed");
            e.printStackTrace();
//            throw new RuntimeException(e);
        }

        try {
            assert resultSet != null;
            if (resultSet.next()) {
                user = username;
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void logout() {
        user = "";
    }

    public void migrate() {
        String SQL_QUERY = """
                DROP TABLE IF EXISTS "Users";
                CREATE TABLE "Users"
                (
                    username character varying NOT NULL,
                    password character varying NOT NULL,
                    CONSTRAINT "Users_pkey" PRIMARY KEY (username)
                );
                
                INSERT INTO "Users" (username, password) VALUES ('test_user', '123');
                
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
                );
                
                INSERT INTO "Professors" (full_name, birth_date, address, email, phone_number) VALUES
                ('Мокеев', '1980-01-01',  '123', '123', '123');
                
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
                
                DROP TABLE IF EXISTS "Groups";
                CREATE TABLE "Groups"
                (
                phone_number character varying NOT NULL,
                direction character varying NOT NULL,
                number_of_students integer,
                group_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 1000000 CACHE 1 ),
                number_group integer NOT NULL
                );
                
                DROP TABLE IF EXISTS "Subjects";
                CREATE TABLE "Subjects"
                (
                subject_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 1000000 CACHE 1 ),
                subject_name character varying(255) NOT NULL,
                description text
                );
                """;

        try {
            PreparedStatement pst = connection.prepareStatement(SQL_QUERY);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Group> getGroups() {
        String sql = "SELECT * FROM \"Groups\"";

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert statement != null;
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Statement failed");
            e.printStackTrace();
        }

        List<Group> groupList = new ArrayList<>();
        try {
            while(resultSet.next()) {
                Group group = new Group(
                        resultSet.getInt("group_id"),
                        resultSet.getInt("number_group"),
                        resultSet.getInt("number_of_students"),
                        resultSet.getString("direction"),
                        resultSet.getString("phone_number")
                );
                groupList.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupList;
    }
    public boolean addGroup(Group group) {
        String sql = "INSERT INTO \"Groups\" (number_group, number_of_students, direction, phone_number) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setInt(1, group.getGroupNum());
            preparedStatement.setInt(2, group.getStudentsNum());
            preparedStatement.setString(3, group.getDirection());
            preparedStatement.setString(4, group.getPhoneNum());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean updateGroup(Group group) {
        String sql = "UPDATE \"Groups\"\n" +
                "SET number_group = ?, number_of_students = ?, direction = ?, phone_number = ?\n" +
                "WHERE group_id = ?;";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setInt(1, group.getGroupNum());
            preparedStatement.setInt(2, group.getStudentsNum());
            preparedStatement.setString(3, group.getDirection());
            preparedStatement.setString(4, group.getPhoneNum());
            preparedStatement.setInt(5, group.getGroupId());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean deleteGroup(int id) {
        String sql = "DELETE FROM \"Groups\"\n" +
                "WHERE group_id = ?;";

        return executeDelete(id, sql);
    }

    public List<Professor> getProfessors() {
        String sql = "SELECT * FROM \"Professors\"";

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert statement != null;
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Statement failed");
            e.printStackTrace();
        }

        List<Professor> professorList = new ArrayList<>();
        try {
            while(resultSet.next()) {
                List<Integer> subjects = new ArrayList<>();

                try {
                    var objectArray = (Object []) resultSet.getArray("subjects").getArray();
                    subjects = Arrays.stream(objectArray).map(ob->(Integer)ob).collect(Collectors.toList());
                } catch (NullPointerException nex) {

                }

                Professor professor = new Professor(
                        resultSet.getInt("professor_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("phone_number"),
                        resultSet.getDate("birth_date").toLocalDate(),
                        resultSet.getString("address"),
                        resultSet.getString("email"),
                        subjects
                );
                professorList.add(professor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return professorList;
    }
    public boolean addProfessor(Professor professor) {
        String sql = "INSERT INTO \"Professors\" (full_name, phone_number, birth_date, address, email, subjects) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setString(1, professor.getFullName());
            preparedStatement.setString(2, professor.getPhoneNum());
            preparedStatement.setDate(3, Date.valueOf(professor.getBirthDate()));
            preparedStatement.setString(4, professor.getAddress());
            preparedStatement.setString(5, professor.getEmail());
            Array array = connection.createArrayOf("integer", professor.getSubjects().toArray());
            preparedStatement.setArray(6, array);

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean updateProfessor(Professor professor) {
        String sql = "UPDATE \"Professors\"\n" +
                "SET full_name = ?, phone_number = ?, birth_date = ?, address = ?, email = ?, subjects = ?\n" +
                "WHERE professor_id = ?;";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;

            Array subjectsArray = connection.createArrayOf("int", professor.getSubjects().toArray());
            preparedStatement.setString(1, professor.getFullName());
            preparedStatement.setString(2, professor.getPhoneNum());
            preparedStatement.setDate(3, Date.valueOf(professor.getBirthDate()));
            preparedStatement.setString(4, professor.getAddress());
            preparedStatement.setString(5, professor.getEmail());
            preparedStatement.setArray(6, subjectsArray);
            preparedStatement.setInt(7, professor.getProfessorId());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean deleteProfessor(int id) {
        String sql = "DELETE FROM \"Professors\"\n" +
                "WHERE professor_id = ?;";

        return executeDelete(id, sql);
    }

    public List<Subject> getSubjects() {
        String sql = "SELECT * FROM \"Subjects\"";

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert statement != null;
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Statement failed");
            e.printStackTrace();
        }

        List<Subject> subjectList = new ArrayList<>();
        try {
            while(resultSet.next()) {
                Subject subject = new Subject(
                        resultSet.getInt("subject_id"),
                        resultSet.getString("subject_name"),
                        resultSet.getString("description")
                );
                subjectList.add(subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjectList;
    }
    public boolean addSubject(Subject subject) {
        String sql = "INSERT INTO \"Subjects\" (subject_name, description) VALUES (?, ?)";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setString(1, subject.getSubjectName());
            preparedStatement.setString(2, subject.getDescription());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean updateSubject(Subject subject) {
        String sql = "UPDATE \"Subjects\"\n" +
                "SET subject_name = ?, description = ?\n" +
                "WHERE subject_id = ?;";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setString(1, subject.getSubjectName());
            preparedStatement.setString(2, subject.getDescription());
            preparedStatement.setInt(3, subject.getSubjectId());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean deleteSubject(int id) {
        String sql = "DELETE FROM \"Subjects\"\n" +
                "WHERE subject_id = ?;";

        return executeDelete(id, sql);
    }


    public List<Curriculum> getCurricula() {
        String sql = "SELECT * FROM \"Curricula\"";

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert statement != null;
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Statement failed");
            e.printStackTrace();
        }

        List<Curriculum> curriculumList = new ArrayList<>();
        try {
            while(resultSet.next()) {
                Curriculum curriculum = new Curriculum(
                        resultSet.getInt("curriculum_id"),
                        resultSet.getInt("lecture_hours"),
                        resultSet.getInt("practice_hours"),
                        resultSet.getString("direction"),
                        resultSet.getInt("subject_id"),
                        resultSet.getInt("semester")
                );
                curriculumList.add(curriculum);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return curriculumList;
    }
    public boolean addCurriculum(Curriculum curriculum) {
        String sql = "INSERT INTO \"Curricula\" (lecture_hours, practice_hours, direction, subject_id, semester) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setInt(1, curriculum.getLectureHours());
            preparedStatement.setInt(2, curriculum.getPracticeHours());
            preparedStatement.setString(3, curriculum.getDirection());
            preparedStatement.setInt(4, curriculum.getSubjectId());
            preparedStatement.setInt(5, curriculum.getSemester());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean updateCurriculum(Curriculum curriculum) {
        String sql = "UPDATE \"Curricula\"\n" +
                "SET lecture_hours = ?, practice_hours = ?, direction = ?, subject_id = ?, semester = ?\n" +
                "WHERE curriculum_id = ?;";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setInt(1, curriculum.getLectureHours());
            preparedStatement.setInt(2, curriculum.getPracticeHours());
            preparedStatement.setString(3, curriculum.getDirection());
            preparedStatement.setInt(4, curriculum.getSubjectId());
            preparedStatement.setInt(5, curriculum.getSemester());
            preparedStatement.setInt(6, curriculum.getCurriculumId());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean deleteCurriculum(int id) {
        String sql = "DELETE FROM curricula\n" +
                "WHERE curriculum_id = ?;";

        return executeDelete(id, sql);
    }

    private boolean executeDelete(int id, String sql) {
        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            preparedStatement.setInt(1, id);

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete failed");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public boolean truncateSubjects() {
        String sql = "TRUNCATE TABLE \"Subjects\"";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete failed");
            e.printStackTrace();
        }

        return rows > 0;
    }

    public boolean truncateCurricula() {
        String sql = "TRUNCATE TABLE \"Curricula\"";

        PreparedStatement preparedStatement = null;
        int rows = 0;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            assert preparedStatement != null;
            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete failed");
            e.printStackTrace();
        }

        return rows > 0;
    }

    private List<Subject> subjects = new ArrayList<>();

    public List<Subject> getLoadedSubjects() {
        return this.subjects;
    }
    public void setSubjects() {
        this.subjects = getSubjects();
    }

    private List<Professor> professors = new ArrayList<>();

    public List<Professor> getLoadedProfessors() {
        return this.professors;
    }
    public void setProfessors() {
        this.professors = getProfessors();
    }
}