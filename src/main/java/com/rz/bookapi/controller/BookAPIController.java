package com.rz.bookapi.controller;

import com.rz.bookapi.dao.BookDAO;
import com.rz.bookapi.model.Book;
import com.rz.bookapi.model.BookList;
import com.rz.bookapi.utils.InputStreamUtils;
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

    private BookDAO bookDAO;
    private ResponseWriter responseWriter;
    private RequestReader requestReader;

    private InputStreamUtils inputStreamUtils;
    @Override
    public void init() {
        bookDAO = new BookDAO();
        responseWriter = new ResponseWriter();
        requestReader = new RequestReader();
        inputStreamUtils = new InputStreamUtils();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();

        String format = request.getHeader("Accept");

        BookList allBooks = new BookList();

        String idParam = request.getParameter("id");
        String titleParam = request.getParameter("title");

        int requestedID;

        if (idParam != null) {
            try {
                requestedID = Integer.parseInt(idParam);
                allBooks.setBooks(List.of(bookDAO.getBookByID(requestedID)));
            } catch (NumberFormatException e) {
                response.setStatus(500);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID not found");
                return;
            }
        } else if (titleParam != null) {
            try {
                allBooks.setBooks(List.of(bookDAO.getBookByTitle(titleParam)));
            } catch (NumberFormatException e) {
                response.setStatus(500);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title not found");
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

        try {
            inputStream = request.getInputStream();

            String requestData = inputStreamUtils.getStringFromInputStream(inputStream);
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

        bookDAO.deleteBook(Integer.parseInt(idParam));

        response.setStatus(200);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {

        String contentType = request.getHeader("Content-Type");

        InputStream inputStream;

        try {
            inputStream = request.getInputStream();

            String requestData = inputStreamUtils.getStringFromInputStream(inputStream);

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
