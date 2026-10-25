package com.scc.dao;

import com.scc.model.user;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDao{

    public UserDao(){
        try (Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement()){

            defaultAdminGenerator();
        }catch(SQLException e){
            //System.err.println("Error seeding default admin: " + e.getMessage());

        }
    }

    public void defaultAdminGenerator() {
        String checkSQL = "SELECT COUNT(*) FROM users WHERE roles = 'admin'";
        String insertSQL = "INSERT INTO users (username, password, roles, status) VALUES ('admin', 'admin123', 'ADMIN', 'approved')";

            try (Connection con = DatabaseConnection.getConnection();
                 Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(checkSQL)) {

            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertSQL);
                System.out.println(" --> Default Admin created (Username: admin | Password: admin123)");
            }
        } catch (SQLException e) {
            //System.out.println("Error seeding default admin: " + e.getMessage());
        }
    }



    public boolean registerUser(String username, String password, String role){

        String sql = "INSERT INTO Users(username ,password ,roles,status ) VALUES (?, ?, ?,'PENDING' )";

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1,username);
            pstmt.setString(2,password);
            pstmt.setString(3,role.toUpperCase());
            pstmt.executeUpdate();
            System.out.println();
            System.out.println("-> Successful registration , Waiting for Approval!");
            return true;



        }catch (SQLException e){
            System.err.println("Failed: " + e.getMessage());
            return false;



        }



    }
    public user login(String username, String password) {
        String sql = "SELECT user_id, username, password, roles, status FROM users WHERE username = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("pending".equalsIgnoreCase(status)) {
                        System.out.println("\nAccount is pending, waiting for approval.");
                        return null;
                    }

                    return new user(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                } else {
                    System.out.println("\nInvalid username or password.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return null;
    }
    public List<user> getAllUsers() {
        List<user> userList = new ArrayList<>();
        String sql = "SELECT user_id, username, password, roles, status FROM users";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                user User = new user(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                );
                userList.add(User);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
        return userList;
    }
    public List<user> getPendingUsers() {
        List<user> pendingList = new ArrayList<>();
        String sql = "SELECT user_id, username, password, roles, status FROM users WHERE status = 'PENDING'";

        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                user User = new user(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                );
                pendingList.add(User);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching pending users: " + e.getMessage());
        }
        return pendingList;
    }
    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET status = 'APPROVED' WHERE user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("User ID " + userId + " approved successfully.");
                return true;
            } else {
                System.out.println("User ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error approving user: " + e.getMessage());
        }
        return false;
    }
}