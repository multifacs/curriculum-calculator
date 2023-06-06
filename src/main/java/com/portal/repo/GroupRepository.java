package com.portal.repo;

import com.portal.hikari.DataSource;
import com.portal.model.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupRepository {
    public GroupRepository() {
    }

    public void createTable() throws SQLException {
        String SQL_QUERY = "DROP TABLE \"Groups\";\n" +
                "\n" +
                "CREATE TABLE \"Groups\"\n" +
                "(\n" +
                "phone_number character varying NOT NULL,\n" +
                "direction character varying NOT NULL,\n" +
                "number_of_students integer,\n" +
                "group_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 1000000 CACHE 1 ),\n" +
                "number_group integer NOT NULL\n" +
                ") ";

        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement( SQL_QUERY );) {
            pst.executeUpdate();
        }
    }

    public List<Group> getGroups() throws SQLException {
        String SQL_QUERY = "SELECT * FROM \"Groups\"";
        List<Group> groupList = new ArrayList<>();
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);
             ResultSet rs = pst.executeQuery();) {
            Group group;
            while (rs.next()) {
                group = new Group(
                        rs.getInt("group_id"),
                        rs.getInt("number_group"),
                        rs.getInt("number_of_students"),
                        rs.getString("direction"),
                        rs.getString("phone_number")
                );
                groupList.add(group);
            }
        }
        return groupList;
    }

    public boolean createGroup(Group group) throws SQLException {
        String SQL_QUERY = "INSERT INTO \"Groups\" (number_group, number_of_students, direction, phone_number) VALUES (?, ?, ?, ?)";
        int rows = 0;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, group.getGroupNum());
            pst.setInt(2, group.getStudentsNum());
            pst.setString(3, group.getDirection());
            pst.setString(4, group.getPhoneNum());

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean updateGroup(Group group) throws SQLException {
        String SQL_QUERY = """
                UPDATE "Groups"
                SET number_group = ?, number_of_students = ?, direction = ?, phone_number = ?
                WHERE group_id = ?;""";
        int rows = 0;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, group.getGroupNum());
            pst.setInt(2, group.getStudentsNum());
            pst.setString(3, group.getDirection());
            pst.setString(4, group.getPhoneNum());
            pst.setInt(5, group.getGroupId());

            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

    public boolean deleteGroup(Group group) throws SQLException {
        String SQL_QUERY = "DELETE FROM \"Groups\"\n" +
                "WHERE group_id = ?;";

        int rows = 0;
        try (Connection con = DataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(SQL_QUERY);) {

            pst.setInt(1, group.getGroupId());
            rows = pst.executeUpdate();
        }
        return rows > 0;
    }

}
