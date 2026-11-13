package com.library.service;

import com.library.model.Member;
import com.library.model.Reportable;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** IO package demo: text files (BufferedWriter/Reader), append logging, and object serialisation. */
public class FileManager {
    private static final String LOG_FILE = "activity.log";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Appends one line to the activity log (FileWriter in append mode). */
    public static synchronized void log(String message) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            pw.println("[" + LocalDateTime.now().format(FMT) + "] " + message);
        } catch (IOException e) {
            System.out.println("(could not write log: " + e.getMessage() + ")");
        }
    }

    /** Reads the last N lines of the log back (FileReader + BufferedReader). */
    public static List<String> readLog(int lastN) throws IOException {
        List<String> all = new ArrayList<>();
        File f = new File(LOG_FILE);
        if (!f.exists()) {
            return all;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                all.add(line);
            }
        }
        int from = Math.max(0, all.size() - lastN);
        return all.subList(from, all.size());
    }

    /** Writes any list of Reportable objects (books, members, issues) into a text report. */
    public static void writeReport(String fileName, String title, List<? extends Reportable> items)
            throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write("==== " + title + " ====");
            bw.newLine();
            bw.write("Generated: " + LocalDateTime.now().format(FMT));
            bw.newLine();
            bw.newLine();
            for (Reportable r : items) {
                bw.write(r.toReportLine());
                bw.newLine();
            }
        }
    }

    /** Object serialisation: saves all members into a binary backup file. */
    public static void backupMembers(String fileName, List<Member> members) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(new ArrayList<>(members));
        }
    }

    /** De-serialisation: reads the binary backup back into objects. */
    @SuppressWarnings("unchecked")
    public static List<Member> readMemberBackup(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<Member>) ois.readObject();
        }
    }
}
