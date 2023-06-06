package com.portal.repo;

import com.portal.hikari.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public UserRepository() {
    }

    public void createTable() throws SQLException {
        String SQL_QUERY = "DROP TABLE IF EXISTS \"Users\";\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS \"Users\"\n" +
                "(\n" +
                "    username character varying NOT NULL,\n" +
                "    password character varying NOT NULL,\n" +
                "    CONSTRAINT \"Users_pkey\" PRIMARY KEY (username)\n" +
                ")";

        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement( SQL_QUERY );) {
            pst.executeUpdate();
        }
    }

    public boolean checkUser(String username, String password) throws SQLException {
        String SQL_QUERY = "SELECT * FROM \"Users\" WHERE username = ? AND password = ?";

        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement( SQL_QUERY );) {
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet resultSet = pst.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
