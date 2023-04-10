package com.rz.bookapi.dao;

import com.rz.bookapi.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BookDAO {

    Book book = null;
    Connection conn = null;
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

            thisBook = new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("date"), rs.getString("genres"), rs.getString("characters"), rs.getString("synopsis"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return thisBook;
    }


    public List<Book> getAllBooks() {

        List<Book> allBooks = new ArrayList<>();
        openConnection();

        try {
            PreparedStatement getAllBooks = conn.prepareStatement("select * from books;");
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

    public void addBook(Book book) {
        openConnection();
        try {
            PreparedStatement addBook = conn.prepareStatement("INSERT INTO books (title, author, date, genres, characters, synopsis) VALUES (?,?,?,?,?,?)");
            addBook.setString(1, book.getTitle());
            addBook.setString(2, book.getAuthor());
            addBook.setString(3, book.getDate());
            addBook.setString(4, book.getGenres());
            addBook.setString(5, book.getCharacters());
            addBook.setString(6, book.getSynopsis());

            addBook.executeUpdate();

            addBook.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    public boolean deleteBook(int id) {
        openConnection();
        boolean deleted = false;
        try {
            PreparedStatement deleteBook = conn.prepareStatement("DELETE FROM books WHERE id = ?");
            deleteBook.setInt(1, id);

            deleteBook.executeUpdate();
            deleted = true;
            deleteBook.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
        return deleted;
    }

    public void updateBook(Book book) {
        openConnection();
        try {
            PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET title = ?, author = ?, date = ?, genres = ?, characters = ?, synopsis = ? WHERE id = ?;");
            updateBook.setString(1, book.getTitle());
            updateBook.setString(2, book.getAuthor());
            updateBook.setString(3, book.getDate());
            updateBook.setString(4, book.getGenres());
            updateBook.setString(5, book.getCharacters());
            updateBook.setString(6, book.getSynopsis());
            updateBook.setInt(7, book.getId());

            updateBook.executeUpdate();

            updateBook.close();
            closeConnection();
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

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
