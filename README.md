# Library Management System (Java + JDBC)
OOPs in Java mini project - console application with a real database (JDBC).

## What it does
Register students / faculty, add books, issue and return books, calculate fines for late returns,
search the catalogue, see overdue books, export reports and read the activity log.
A background thread reminds you when books are overdue.

## How to run (Windows)
1. Install JDK 25 or newer (check with `javac -version`).
2. Open the project folder in a terminal and double-click / run `run.bat`
   (Mac/Linux: `./run.sh`).
3. The database file `library.db` is created automatically on the first run. No server needed.

VS Code / IntelliJ: open the folder, mark `src` as the sources root, add `lib/sqlite-jdbc.jar`
to the classpath and run `com.library.Main`.

## Web frontend (Spring Boot)
The project also includes a browser-based, server-rendered frontend built with Java 25, Spring Boot, and Thymeleaf (no browser-side JavaScript).
It runs locally and provides an overview dashboard, book search and registration, member registration,
and book issue/return screens. The existing console application remains available.

1. Install JDK 25 or newer.
2. From the project folder, run `run-web.bat` (Mac/Linux: `./run-web.sh`). The Maven Wrapper downloads Maven and the project dependencies the first time.
3. Open [http://localhost:8080](http://localhost:8080) in your browser.

The web app and console app use the same `library.db` when launched from the project folder. The web server binds to
`127.0.0.1` and is intended for local use, not direct public deployment.

## Syllabus coverage
| Syllabus topic | Where in the project |
|---|---|
| Objects, classes, constructors, overloading | `Book` (3 constructors), `LibraryService.addBook` (3 overloads) |
| Abstract classes, inheritance, overriding, `super` | `Member` (abstract) -> `Student`, `Faculty` override `getBorrowLimit()`, `getFinePerDay()` |
| Interfaces | `Reportable` implemented by `Book`, `Member`, `IssueRecord` |
| Packages & access control | `model`, `db`, `service`, `exception` packages; private fields + getters |
| Exception handling (try/catch/finally/throw/throws, hierarchy) | custom `LibraryException` -> `BookNotAvailableException`, `LimitExceededException`, `NotFoundException` |
| IO package, streams, serialisation | `FileManager`: BufferedWriter/Reader, FileWriter (append log), ObjectOutputStream/ObjectInputStream (`members.ser`) |
| Multithreading, synchronisation | `OverdueChecker` (Runnable on a daemon thread), `synchronized` issue/return methods |
| JDBC (mandatory) | `DBConnection` (Singleton), DAO classes with `PreparedStatement`, `ResultSet`, transactions (commit/rollback) |
| Data types, arrays/collections, control structures | `List`, `ArrayList`, switch, loops throughout `Main` |

## Project structure
```
src/com/library/
  Main.java                      menu / user interface
  model/      Reportable, Book, Member (abstract), Student, Faculty, IssueRecord
  exception/  LibraryException, BookNotAvailableException, LimitExceededException, NotFoundException
  db/         DBConnection, BookDAO, MemberDAO, IssueDAO
  service/    LibraryService, FileManager, OverdueChecker
web/                              Spring Boot + Thymeleaf web frontend
lib/sqlite-jdbc.jar              JDBC driver
schema_mysql.sql                 only for the MySQL option
```

## Rules built into the system
* Students can borrow 3 books (fine Rs.2/day); faculty 5 books (fine Rs.1/day).
* A book with no free copies cannot be issued.
* Issuing/returning is one database transaction: the issue record and the stock count change together or not at all.

## Quick demo script
1. Option 1 - add a book (`Clean Code`, `Robert Martin`, `Programming`, 2 copies).
2. Option 2 - register a `student` and a `faculty` member.
3. Option 5 - issue book 1 to member 1 with `14` days.
4. Option 5 - issue again with `-3` days (backdated, to demo overdue).
5. Option 9 - see overdue books, option 6 - return it and see the fine (3 days x Rs.2 = Rs.6).
6. Option 10 - export reports, option 11 - view the activity log.

## Using MySQL instead of SQLite (optional)
1. Run `schema_mysql.sql` in MySQL.
2. Add the MySQL Connector/J jar to `lib/` and to the classpath in `run.bat`.
3. In `db/DBConnection.java` comment the SQLite block and un-comment the MySQL block (set your password).
4. In `Main.java` comment out the line `DBConnection.getInstance().initSchema();`.

## Ideas to extend
Login screen, GUI with Swing, Spring Boot + Spring JDBC Template version (Unit 3 topic), email reminders.
