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
    String password = "root";

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
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

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

    public List<Group> getGroups() {
        String sql = "SELECT * FROM groups";

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
        String sql = "INSERT INTO groups (number_group, number_of_students, direction, phone_number) VALUES (?, ?, ?, ?)";

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
        String sql = "UPDATE groups\n" +
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
        String sql = "DELETE FROM groups\n" +
                "WHERE group_id = ?;";

        return executeDelete(id, sql);
    }

    public List<Professor> getProfessors() {
        String sql = "SELECT * FROM professors";

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
                List<Integer> subjects;
                var objectArray = (Object []) resultSet.getArray("subjects").getArray();
                subjects = Arrays.stream(objectArray).map(ob->(Integer)ob).collect(Collectors.toList());

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
        String sql = "INSERT INTO professors (full_name, phone_number, birth_date, address, email, subjects) VALUES (?, ?, ?, ?, ?, ?)";

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
        String sql = "UPDATE professors\n" +
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
        String sql = "DELETE FROM professors\n" +
                "WHERE professor_id = ?;";

        return executeDelete(id, sql);
    }


    public List<Subject> getSubjects() {
        String sql = "SELECT * FROM subjects";

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
                        resultSet.getString("description"),
                        resultSet.getInt("professor_id")
                );
                subjectList.add(subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjectList;
    }
    public boolean addSubject(Subject subject) {
        String sql = "INSERT INTO subjects (subject_name, description, professor_id) VALUES (?, ?, ?)";

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
            preparedStatement.setInt(3, subject.getProfessorId());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean updateSubject(Subject subject) {
        String sql = "UPDATE subjects\n" +
                "SET subject_name = ?, description = ?, professor_id = ?\n" +
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
            preparedStatement.setInt(3, subject.getProfessorId());
            preparedStatement.setInt(4, subject.getSubjectId());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean deleteSubject(int id) {
        String sql = "DELETE FROM subjects\n" +
                "WHERE subject_id = ?;";

        return executeDelete(id, sql);
    }


    public List<Curriculum> getCurricula() {
        String sql = "SELECT * FROM curriculums";

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
                        resultSet.getInt("subject_id")
                );
                curriculumList.add(curriculum);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return curriculumList;
    }
    public boolean addCurriculum(Curriculum curriculum) {
        String sql = "INSERT INTO curriculums (lecture_hours, practice_hours, direction, subject_id) VALUES (?, ?, ?, ?)";

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

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean updateCurriculum(Curriculum curriculum) {
        String sql = "UPDATE curriculums\n" +
                "SET lecture_hours = ?, practice_hours = ?, direction = ?, subject_id = ?\n" +
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
            preparedStatement.setInt(5, curriculum.getCurriculumId());

            System.out.println(preparedStatement);

            rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update failed");
            e.printStackTrace();
        }

        return rows > 0;
    }
    public boolean deleteCurriculum(int id) {
        String sql = "DELETE FROM curriculums\n" +
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
        String sql = "TRUNCATE TABLE subjects";

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
        String sql = "TRUNCATE TABLE curriculums";

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
}