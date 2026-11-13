package com.library.web;

import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class LibraryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryController.class);
    private final LibraryService library = new LibraryService();

    @GetMapping("/")
    public String dashboard(Model model) throws SQLException {
        List<Book> books = library.allBooks();
        List<IssueRecord> issues = library.allIssues();
        model.addAttribute("bookCount", books.size());
        model.addAttribute("availableCount", books.stream().mapToInt(Book::getAvailable).sum());
        model.addAttribute("memberCount", library.allMembers().size());
        model.addAttribute("activeCount", issues.stream().filter(issue -> !issue.isReturned()).count());
        model.addAttribute("overdueIssues", library.overdue());
        return "index";
    }

    @GetMapping("/books")
    public String books(@RequestParam(defaultValue = "") String keyword, Model model) throws SQLException {
        model.addAttribute("books", keyword.isBlank() ? library.allBooks() : library.searchBooks(keyword.trim()));
        model.addAttribute("keyword", keyword);
        return "books";
    }

    @PostMapping("/books")
    public String addBook(@RequestParam String title, @RequestParam String author,
                          @RequestParam(defaultValue = "General") String category,
                          @RequestParam int copies, RedirectAttributes redirect) {
        if (title.isBlank() || author.isBlank() || copies < 1) {
            redirect.addFlashAttribute("error", "Enter a title, an author, and at least one copy.");
            return "redirect:/books";
        }
        try {
            library.addBook(title.trim(), author.trim(), category.isBlank() ? "General" : category.trim(), copies);
            redirect.addFlashAttribute("success", "Book added to the catalogue.");
        } catch (SQLException e) {
            return databaseError("add book", e, redirect, "/books");
        }
        return "redirect:/books";
    }

    @GetMapping("/members")
    public String members(Model model) throws SQLException {
        model.addAttribute("members", library.allMembers());
        return "members";
    }

    @PostMapping("/members")
    public String addMember(@RequestParam String type, @RequestParam String name,
                            @RequestParam(defaultValue = "") String email, RedirectAttributes redirect) {
        if ((!type.equalsIgnoreCase("student") && !type.equalsIgnoreCase("faculty")) || name.isBlank()) {
            redirect.addFlashAttribute("error", "Choose student or faculty and enter a member name.");
            return "redirect:/members";
        }
        try {
            library.registerMember(type, name.trim(), email.trim());
            redirect.addFlashAttribute("success", "Member registered.");
        } catch (SQLException e) {
            return databaseError("register member", e, redirect, "/members");
        }
        return "redirect:/members";
    }

    @GetMapping("/loans")
    public String loans(Model model) throws SQLException {
        List<IssueRecord> issues = library.allIssues();
        model.addAttribute("books", library.allBooks());
        model.addAttribute("members", library.allMembers());
        model.addAttribute("issues", issues);
        model.addAttribute("today", LocalDate.now());
        return "loans";
    }

    @PostMapping("/loans")
    public String issueBook(@RequestParam int memberId, @RequestParam int bookId,
                            @RequestParam int days, RedirectAttributes redirect) {
        if (days < 1 || days > 365) {
            redirect.addFlashAttribute("error", "Loan length must be between 1 and 365 days.");
            return "redirect:/loans";
        }
        try {
            library.issueBook(memberId, bookId, days);
            redirect.addFlashAttribute("success", "Book issued successfully.");
        } catch (LibraryException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (SQLException e) {
            return databaseError("issue book", e, redirect, "/loans");
        }
        return "redirect:/loans";
    }

    @PostMapping("/loans/{issueId}/return")
    public String returnBook(@PathVariable int issueId, RedirectAttributes redirect) {
        try {
            double fine = library.returnBook(issueId);
            redirect.addFlashAttribute("success",
                    fine > 0 ? String.format("Book returned. Fine due: Rs. %.2f", fine) : "Book returned. No fine due.");
        } catch (LibraryException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (SQLException e) {
            return databaseError("return book", e, redirect, "/loans");
        }
        return "redirect:/loans";
    }

    private String databaseError(String operation, SQLException exception,
                                 RedirectAttributes redirect, String destination) {
        LOGGER.error("Could not {} in the library database", operation, exception);
        redirect.addFlashAttribute("error", "The operation could not be completed because of a database error.");
        return "redirect:" + destination;
    }
}
