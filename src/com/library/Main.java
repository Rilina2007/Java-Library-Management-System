package com.library;

import com.library.db.DBConnection;
import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.service.FileManager;
import com.library.service.LibraryService;
import com.library.service.OverdueChecker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final LibraryService service = new LibraryService();

    public static void main(String[] args) {
        try {
            DBConnection.getInstance().initSchema();
        } catch (SQLException e) {
            System.out.println("Could not connect to the database: " + e.getMessage());
            return;
        }

        OverdueChecker checker = new OverdueChecker(60);
        Thread t = new Thread(checker, "overdue-checker");
        t.setDaemon(true);
        t.start();

        System.out.println("=====================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM (Java)  ");
        System.out.println("=====================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> registerMember();
                    case 3 -> listBooks();
                    case 4 -> searchBooks();
                    case 5 -> issueBook();
                    case 6 -> returnBook();
                    case 7 -> memberLoans();
                    case 8 -> listMembers();
                    case 9 -> showOverdue();
                    case 10 -> exportReports();
                    case 11 -> showLog();
                    case 12 -> removeBook();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid option, try again.");
                }
            } catch (LibraryException e) {
                System.out.println("Not allowed: " + e.getMessage());   // our own rule violations
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            } finally {
                System.out.println();
            }
        }
        checker.stop();
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("1. Add book            7. View a member's loans");
        System.out.println("2. Register member     8. List members");
        System.out.println("3. List books          9. Show overdue books");
        System.out.println("4. Search books       10. Export reports + backup");
        System.out.println("5. Issue book         11. View activity log");
        System.out.println("6. Return book        12. Remove book");
        System.out.println("0. Exit");
    }

    private static void addBook() throws SQLException {
        String title = readLine("Title: ");
        String author = readLine("Author: ");
        String category = readLine("Category (press Enter to skip): ");
        int id;
        if (category.isEmpty()) {
            id = service.addBook(title, author);                                 // overload #1
        } else {
            id = service.addBook(title, author, category, readInt("Copies: "));  // overload #2
        }
        System.out.println("Book added with ID " + id);
    }

    private static void registerMember() throws SQLException {
        String type = readLine("Type (student/faculty): ");
        if (!type.equalsIgnoreCase("student") && !type.equalsIgnoreCase("faculty")) {
            System.out.println("Type must be 'student' or 'faculty'.");
            return;
        }
        String name = readLine("Name: ");
        String email = readLine("Email: ");
        System.out.println("Member registered with ID " + service.registerMember(type, name, email));
    }

    private static void listBooks() throws SQLException {
        printBooks(service.allBooks());
    }

    private static void searchBooks() throws SQLException {
        printBooks(service.searchBooks(readLine("Search title/author: ")));
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        System.out.printf("%-4s %-30s %-20s %-12s %s%n", "ID", "Title", "Author", "Category", "Avail");
        for (Book b : books) {
            System.out.println(b);
        }
    }

    private static void issueBook() throws LibraryException, SQLException {
        int memberId = readInt("Member ID: ");
        int bookId = readInt("Book ID: ");
        int days = readInt("Days allowed (14 normal; negative = backdated, for demo): ");
        service.issueBook(memberId, bookId, days);
        System.out.println("Book issued successfully.");
    }

    private static void returnBook() throws LibraryException, SQLException {
        double fine = service.returnBook(readInt("Issue ID: "));
        System.out.println(fine > 0 ? "Returned late. Fine to pay: Rs." + fine : "Returned on time. No fine.");
    }

    private static void memberLoans() throws SQLException {
        List<IssueRecord> list = service.activeIssues(readInt("Member ID: "));
        if (list.isEmpty()) {
            System.out.println("No active loans.");
        }
        list.forEach(System.out::println);
    }

    private static void listMembers() throws SQLException {
        List<Member> list = service.allMembers();
        if (list.isEmpty()) {
            System.out.println("No members yet.");
        }
        list.forEach(System.out::println);
    }

    private static void showOverdue() throws SQLException {
        List<IssueRecord> list = service.overdue();
        if (list.isEmpty()) {
            System.out.println("No overdue books.");
        }
        list.forEach(System.out::println);
    }

    private static void exportReports() throws SQLException, IOException {
        FileManager.writeReport("books_report.txt", "BOOKS", service.allBooks());
        FileManager.writeReport("members_report.txt", "MEMBERS", service.allMembers());
        FileManager.writeReport("issues_report.txt", "ISSUE HISTORY", service.allIssues());
        FileManager.backupMembers("members.ser", service.allMembers());
        System.out.println("Created books_report.txt, members_report.txt, issues_report.txt and members.ser");
    }

    private static void showLog() throws IOException {
        List<String> lines = FileManager.readLog(15);
        if (lines.isEmpty()) {
            System.out.println("Log is empty.");
        }
        lines.forEach(System.out::println);
    }

    private static void removeBook() throws LibraryException, SQLException {
        service.removeBook(readInt("Book ID to remove: "));
        System.out.println("Book removed.");
    }

    // ---------- input helpers (robust against typing letters instead of numbers) ----------
    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    private static int readInt(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                if (!sc.hasNextLine()) {
                    return 0;
                }
            }
        }
    }
}
