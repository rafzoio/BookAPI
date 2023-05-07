package com.rz.bookapi.dao;

import com.rz.bookapi.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookDAO {

    private static BookDAO instance;
    private Book book = null;
    private Connection conn = null;
    protected final String user = "zoioraph";
    protected final String password = "hertHopl9";
    // Note none default port used, 6306 not 3306
    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/zoioraph";

    private BookDAO() {
    }

    public static BookDAO getInstance() {
        if (instance == null) {
            instance = new BookDAO();
        }
        return instance;
    }

    /**
     * Method to initialise connection to database.
     */
    private void openConnection() {
        // loading jdbc driver for mysql
        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    /**
     * Method to close connection to database
     */
    private void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to generate a book object from jdbc result set
     * @param rs result set
     * @return Book
     */
    private Book getNextBook(ResultSet rs) {
        Book thisBook;
        try {

            thisBook = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("date"), rs.getString("genres"), rs.getString("characters"), rs.getString("synopsis"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return thisBook;
    }

    /**
     * Gets book from database by id
     * @param id requested id
     * @return Book
     */
    public Book getBookByID(int id) {

        openConnection();
        book = null;

        try {
            PreparedStatement getBookById = conn.prepareStatement("select * from books where id=?;");
            getBookById.setInt(1, id);
            ResultSet rs1 = getBookById.executeQuery();

            while (rs1.next()) {
                book = getNextBook(rs1);
            }

            getBookById.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return book;
    }

    /**
     * Adds new book to database
     * @param book new book
     */
    public void addBook(Book book) {
        openConnection();
        try {
            PreparedStatement addBook = conn.prepareStatement("INSERT INTO books (title, author, date, genres, characters, synopsis) VALUES (?,?,?,?,?,?)");
            populateStatementWithBook(book, addBook);

            addBook.executeUpdate();

            addBook.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    /**
     * Inserts book attributes into prepared SQL statement
     * @param book book to insert
     * @param statement prepared statement
     */
    private static void populateStatementWithBook(Book book, PreparedStatement statement) throws SQLException {
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setString(3, book.getDate());
        statement.setString(4, book.getGenres());
        statement.setString(5, book.getCharacters());
        statement.setString(6, book.getSynopsis());
    }

    /**
     * Deletes a book from the database by id
     * @param id requested id
     */
    public boolean deleteBook(int id) {
        openConnection();
        try {
            PreparedStatement deleteBook = conn.prepareStatement("DELETE FROM books WHERE id = ?");
            deleteBook.setInt(1, id);

            deleteBook.executeUpdate();
            deleteBook.close();
            closeConnection();
            return true;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    /**
     * Updates a book already present in the database.
     * @param book book with updated attributes
     */
    public void updateBook(Book book) {
        openConnection();
        try {
            PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET title = ?, author = ?, date = ?, genres = ?, characters = ?, synopsis = ? WHERE id = ?;");
            populateStatementWithBook(book, updateBook);
            updateBook.setInt(7, book.getId());

            updateBook.executeUpdate();

            updateBook.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    /**
     * Returns a list of books with titles matching the search keyword.
     * @param keyword text string entered by user to be matched with book title
     * @return matchingBooks
     */
    public List<Book> searchBooks(String keyword) {

        List<Book> allBooks = new ArrayList<>();
        openConnection();

        try {
            PreparedStatement getAllBooks = conn.prepareStatement("select * from books where title like ?;");
            getAllBooks.setString(1, "%" + keyword + "%");
            ResultSet rs1 = getAllBooks.executeQuery();

            while (rs1.next()) {
                book = getNextBook(rs1);
                allBooks.add(book);
            }

            getAllBooks.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return allBooks;
    }

    public List<Book> getPageOfBooks(int i, int pageLength) {

        List<Book> allBooks = new ArrayList<>();
        openConnection();

        try {
            PreparedStatement getNumberOfBooks = conn.prepareStatement("select * from books where id >= ? limit ?");
            getNumberOfBooks.setInt(1, i);
            getNumberOfBooks.setInt(2, pageLength);
            ResultSet rs1 = getNumberOfBooks.executeQuery();

            while (rs1.next()) {
                book = getNextBook(rs1);
                allBooks.add(book);
            }

            getNumberOfBooks.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return allBooks;
    }

    /**
     * Returns number of pages of a specified size are needed to display all books contained in the database.
     * @param pageSize length of one page
     * @return number of pages required to display all books
     */
    public int getNumberOfPages(int pageSize) {
        openConnection();
        int count = 0;
        try {
            PreparedStatement countBookTable = conn.prepareStatement("select count(*) from books");
            ResultSet rs1 = countBookTable.executeQuery();
            if (rs1.next()) {
                count = rs1.getInt(1);
            }
            countBookTable.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return Math.floorDiv(count, pageSize) + 1;
    }
}
