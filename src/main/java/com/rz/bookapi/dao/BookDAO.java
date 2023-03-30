package com.rz.bookapi.dao;

import com.rz.bookapi.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookDAO {

    Book oneBook = null;
    Connection conn = null;
    Statement stmt = null;
    String user = "zoioraph";
    String password = "hertHopl9";
    // Note none default port used, 6306 not 3306
    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/zoioraph";

    public BookDAO() {
    }


    private void openConnection() {
        // loading jdbc driver for mysql
        try {
            Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // connecting to database
        try {
            // connection string for demos database, username demos, password demos
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    private void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Book getNextBook(ResultSet rs) {
        Book thisBook;
        try {

            thisBook = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("date"),
                    rs.getString("genres"),
                    rs.getString("characters"),
                    rs.getString("synopsis"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return thisBook;
    }


    public List<Book> getAllBooks() {

        List<Book> allBooks = new ArrayList<>();
        openConnection();

        // Create select statement and execute it
        try {
            String selectSQL = "select * from books";
            ResultSet rs1 = stmt.executeQuery(selectSQL);
            // Retrieve the results
            while (rs1.next()) {
                oneBook = getNextBook(rs1);
                allBooks.add(oneBook);
            }

            stmt.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return allBooks;
    }

    public Book getBookByID(int id) {

        openConnection();
        oneBook = null;
        // Create select statement and execute it
        try {
            String selectSQL = "select * from books where id=" + id;
            ResultSet rs1 = stmt.executeQuery(selectSQL);
            // Retrieve the results
            while (rs1.next()) {
                oneBook = getNextBook(rs1);
            }

            stmt.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return oneBook;
    }

    public void addBook(Book book) {
		openConnection();
		try {
			String query = String.format("INSERT INTO books (id, title, author, date, genres, characters, synopsis) VALUES (%d, '%s', '%s', '%s', '%s', '%s', '%s')", book.getId(), book.getTitle(), book.getAuthor(), book.getDate(), book.getGenres(), book.getCharacters(), book.getSynopsis());
			stmt.executeUpdate(query);
			stmt.close();
			closeConnection();
		} catch (SQLException se) {
            throw new RuntimeException(se);
		}
	}

    public void deleteBook(int id) {
        openConnection();
        try {
            String query = String.format("DELETE FROM books WHERE id = %d", id);
            stmt.executeUpdate(query);
            stmt.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public void updateBook(Book book) {
        openConnection();
        try {
            String query = String.format("UPDATE books SET title = '%s', author = '%s', date = '%s', genres = '%s', characters = '%s', synopsis = '%s' WHERE id = %d",
                    book.getTitle(), book.getAuthor(), book.getDate(), book.getGenres(), book.getCharacters(), book.getSynopsis(), book.getId());
            stmt.executeUpdate(query);
            stmt.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public Book getBookByTitle(String title) {

        openConnection();
        oneBook = null;
        // Create select statement and execute it
        try {
            String selectSQL = "select * from books where title='" + title + "';";
            ResultSet rs1 = stmt.executeQuery(selectSQL);
            // Retrieve the results
            while (rs1.next()) {
                oneBook = getNextBook(rs1);
            }

            stmt.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }

        return oneBook;
    }
}
