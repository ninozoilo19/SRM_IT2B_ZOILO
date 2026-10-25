package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.user;

import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        int choice = 0;
        do {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegister();
                case 3 -> System.out.println("Exiting program...");
                default -> System.out.println("Invalid option!");
            }
        } while (choice != 3);
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        user User = userDao.login(username, password);

        if (User != null) {
            System.out.println("\nWelcome, " + User.username() + "!");

            switch (User.roles().toLowerCase()) {
                case "admin" -> {
                    AdminMenu adminMenu = new AdminMenu(userDao, scanner);
                    adminMenu.display(User);
                }
                default -> System.out.println("Account role not found.");
            }
        }
    }

    private void handleRegister() {
        System.out.println("\n=== USER REGISTRATION ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.println("Select Role:");
        System.out.println("[1]Teacher | [2]Admin ");
        System.out.print("Enter choice: ");

        int roleChoice = 0;
        if (scanner.hasNextInt()) {
            roleChoice = scanner.nextInt();
            scanner.nextLine();
        } else {
            scanner.nextLine();
            System.out.println("Invalid role choice. Registration cancelled.");
            return;
        }

        String role = switch (roleChoice) {
            case 1 -> "Teacher";
            case 2 -> "Admin";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role selected. Registration cancelled.");
            return;
        }


        boolean success = userDao.registerUser(username, password, role);

        if (success) {
            System.out.println("Registration successful! Your account status is 'pending' waiting for admin approval.");
        } else {
            System.out.println("Registration failed. Username might already exist or a database error occurred.");
        }
    }
}