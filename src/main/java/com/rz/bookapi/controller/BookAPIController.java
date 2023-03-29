package com.rz.bookapi.controller;

import com.rz.bookapi.dao.BookDAO;
import com.rz.bookapi.model.Book;
import com.rz.bookapi.model.BookList;
import com.rz.bookapi.utils.RequestReader;
import com.rz.bookapi.utils.ResponseWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;

@WebServlet(name = "BookAPI", value = "/book-api")
public class BookAPIController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        ResponseWriter responseWriter = new ResponseWriter();

        BookDAO bookDAO = new BookDAO();
        BookList allBooks = new BookList();

        String format = request.getHeader("Accept");

        int requestedID;
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                requestedID = Integer.parseInt(idParam);
                allBooks.setBooks(List.of(bookDAO.getBookByID(requestedID)));
            } catch (NumberFormatException e) {
                response.setStatus(500);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID not found");
                return;
            }
        } else {
            allBooks.setBooks(bookDAO.getAllBooks());
        }

        response.setContentType(format);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        out.write(responseWriter.print(allBooks, format));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        String contentType = request.getHeader("Content-Type");

        InputStream inputStream;

        RequestReader requestReader = new RequestReader();
        BookDAO bookDAO = new BookDAO();

        try {
            inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder requestDataBuilder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                requestDataBuilder.append(line);
            }
            String requestData = requestDataBuilder.toString();

            BookList books = requestReader.read(requestData, contentType);

            for(Book book : books.getBooks()) {
                bookDAO.addBook(book);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        response.setStatus(201);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String idParam = request.getParameter("id");

        BookDAO bookDAO = new BookDAO();
        bookDAO.deleteBook(Integer.parseInt(idParam));

        response.setStatus(200);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {

        String contentType = request.getHeader("Content-Type");

        InputStream inputStream;

        RequestReader requestReader = new RequestReader();
        BookDAO bookDAO = new BookDAO();

        try {
            inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder requestDataBuilder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                requestDataBuilder.append(line);
            }
            String requestData = requestDataBuilder.toString();

            BookList books = requestReader.read(requestData, contentType);

            for(Book book : books.getBooks()) {
                bookDAO.updateBook(book);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        response.setStatus(200);
    }
}
