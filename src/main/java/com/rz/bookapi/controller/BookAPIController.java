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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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

        String pageSizeParam = request.getParameter("pageSize");
        String pageNumberParam = request.getParameter("page");

        int pageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 20;
        int pageNumber = (pageNumberParam != null) ? Integer.parseInt(pageNumberParam) : 1;

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
                allBooks.setBooks(bookDAO.searchBooks(titleParam));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title not found");
                return;
            }
        } else {
            allBooks.setBooks(bookDAO.getPageOfBooks(1000 + (pageSize * (pageNumber-1)), pageSize));
        }

        response.setContentType(format);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.setHeader("X-Total-Pages", String.valueOf(bookDAO.getNumberOfPages(pageSize)));
        out.write(responseWriter.print(allBooks, format));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String contentType = request.getHeader("Content-Type");

        InputStream inputStream;

        try {
            inputStream = request.getInputStream();

            String requestData = inputStreamUtils.getStringFromInputStream(inputStream);

            BookList books = requestReader.read(requestData, contentType);

            for (Book book : books.getBooks()) {
                bookDAO.addBook(book);
            }

        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formatting issue");
            return;
        }

        response.setStatus(201);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        boolean bookDeleted = bookDAO.deleteBook(Integer.parseInt(idParam));
        PrintWriter out = response.getWriter();
        if (bookDeleted) {
            out.write("Book with id " + idParam + " deleted.");
            response.setStatus(200);
        } else {
            out.write("Book with id " + idParam + " deleted.");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No book exists of id " + idParam);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String contentType = request.getHeader("Content-Type");

        int id;

        PrintWriter out = response.getWriter();
        InputStream inputStream;

        try {
            inputStream = request.getInputStream();

            String requestData = inputStreamUtils.getStringFromInputStream(inputStream);

            BookList books = requestReader.read(requestData, contentType);

            for (Book book : books.getBooks()) {
                bookDAO.updateBook(book);
                id = book.getId();
            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No book exists of id " + idParam);
        }

        out.write("Book with id " + idParam + " was updated.");
        response.setStatus(200);
    }
}
