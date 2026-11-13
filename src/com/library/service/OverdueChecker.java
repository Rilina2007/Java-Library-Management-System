package com.library.service;

import com.library.db.IssueDAO;
import com.library.model.IssueRecord;

import java.time.LocalDate;
import java.util.List;

/** Multithreading demo: a background thread that periodically scans for overdue books. */
public class OverdueChecker implements Runnable {
    private final IssueDAO issueDAO = new IssueDAO();
    private final long intervalMillis;
    private volatile boolean running = true;

    public OverdueChecker(long intervalSeconds) {
        this.intervalMillis = intervalSeconds * 1000;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervalMillis);
                List<IssueRecord> overdue = issueDAO.findOverdue(LocalDate.now());
                if (!overdue.isEmpty()) {
                    System.out.println("\n[Reminder thread] " + overdue.size()
                            + " book(s) are overdue! Use menu option 9 to see them.");
                    FileManager.log("Reminder: " + overdue.size() + " overdue book(s)");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                System.out.println("[Reminder thread] error: " + e.getMessage());
            }
        }
    }
}
