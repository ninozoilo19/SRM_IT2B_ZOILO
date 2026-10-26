package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.user;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(user currentUser) {
        int choice = 0;
        do {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All Users");
            System.out.println("2. View Pending Approvals");
            System.out.println("3. Approve User Registration");
            System.out.println("4. Update User Role");
            System.out.println("5. Delete User");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1 -> viewAllUsers();
                case 2 -> viewPendingUsers();
                case 3 -> approveUserRegistration();
                case 4 -> System.out.println("Update User Role selected.");
                case 5 -> updateUser(currentUser);
                case 6 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        } while (true);
    }

    private void viewAllUsers() {
        List<user> users = userDao.getAllUsers();
        displayUserTable(users, "ALL REGISTERED USERS");
    }

    private void viewPendingUsers() {
        List<user> pendingUsers = userDao.getPendingUsers();
        displayUserTable(pendingUsers, "PENDING APPROVALS");
    }

    private void approveUserRegistration() {
        List<user> pendingUsers = userDao.getPendingUsers();
        displayUserTable(pendingUsers, "PENDING APPROVALS");

        if (pendingUsers.isEmpty()) {
            return;
        }

        System.out.print("Enter User ID to approve (0 to cancel): ");
        if (scanner.hasNextInt()) {
            int userId = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (userId == 0) {
                System.out.println("Operation canceled.");
                return;
            }

            userDao.approveUser(userId);
        } else {
            scanner.nextLine();
            System.out.println("Invalid input!");
        }
    }

    private void displayUserTable(List<user> users, String title) {
        System.out.println("\n--------------------------------------------------");
        System.out.println(" " + title);
        System.out.println("--------------------------------------------------");

        if (users.isEmpty()) {
            System.out.println("No records found.");
            System.out.println("--------------------------------------------------");
            return;
        }

        System.out.printf("%-5s %-15s %-15s %-10s%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println("--------------------------------------------------");

        for (user u : users) {
            System.out.printf("%-5d %-15s %-15s %-10s%n",
                    u.id(),
                    u.username(),
                    u.roles(),
                    u.status()
            );
        }
        System.out.println("--------------------------------------------------");
    }
    private void updateUser(user currentadmin){
        List<user> users = userDao.getAllUsers();
        displayUserTable(users,"SELECT USER TO UPDATE");

        if(users.isEmpty()){
            return;
        }

        int userId = readIntInput("Enter selected ID to update (0 or to cancel): ");
        if(userId == 0){
            System.out.println(" -> Operation Cancelled.");
            return;
        }
        if(userId == currentadmin.id()){
            System.out.println(" -> [WARNING] Modifying current active Admin is restricted");
        }

        String newRole = readValidRole();
        if(newRole == null){
            System.out.println(" -> Operation Cancelled.");
            return;
        }

        String newStatus  = realValidStatus();
        if(newStatus == null){
            System.out.println(" -> Operation Cancelled. ");
            return;
        }
        userDao.updateUserRoleAndStatus(userId,newRole,newStatus);
    }
    private String readValidRole(){
        while (true){
            System.out.print("Enter Role (TEACHER/ADMIN) [0] to cancel: ");
            String role = scanner.nextLine().trim().toUpperCase();
            if(role.equals("0") || role.equals("CANCEL")){
                return null;
            }
            if(role.equals("TEACHER") || role.equals("ADMIN")){
                return role;
            }
            System.out.println(" -> [ERROR] Invalid Role: ");

        }

    }
    private String realValidStatus(){
        while(true){
            System.out.print("Enter Status (APPROVED / PENDING) [0 to cancel]: ");
            String status = scanner.nextLine().trim().toUpperCase();
            if(status.equals("APPROVED") || status.equals("PENDING")){
                return status;
            }
            System.out.println(" -> [ERROR] Invalid status. Must be APRROVED or PENDING.");
        }
    }
    private int readIntInput(String prompt){
        while(true){
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try{
                return Integer.parseInt(input);
            }catch(NumberFormatException e){
                System.out.println(" -> [ERROR] Please enter a valid numeric ID.");

            }
        }
    }
}