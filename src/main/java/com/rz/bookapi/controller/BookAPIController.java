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

/**
 * Main API Servlet with all crud methods implemented.
 */
@WebServlet(name = "BookAPI", value = "/book-api")
public class BookAPIController extends HttpServlet {

    private BookDAO bookDAO;
    private ResponseWriter responseWriter;
    private RequestReader requestReader;
    private InputStreamUtils inputStreamUtils;

    // initialise all dependencies
    @Override
    public void init() {
        bookDAO = BookDAO.getInstance();
        responseWriter = ResponseWriter.getInstance();
        requestReader = RequestReader.getInstance();
        inputStreamUtils = new InputStreamUtils();
    }

    // get request handler
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();

        // get required request headers and parameters
        String format = request.getHeader("Accept");
        String pageSizeParam = request.getParameter("pageSize");
        String pageNumberParam = request.getParameter("page");
        String idParam = request.getParameter("id");
        String searchString = request.getParameter("title");

        // get pageNumber/pageSize (default to 1/20 if null)
        int pageNumber = (pageNumberParam != null) ? Integer.parseInt(pageNumberParam) : 1;
        int pageSize = (pageSizeParam != null) ? Integer.parseInt(pageSizeParam) : 20;

        BookList bookListResponse = new BookList();

        // check first if id exists, then display one book
        int requestedID;
        if (idParam != null) {
            try {
                requestedID = Integer.parseInt(idParam);
                bookListResponse.setBooks(List.of(bookDAO.getBookByID(requestedID)));
            } catch (NumberFormatException e) {
                response.setStatus(500);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID not found");
                return;
            }
            // then check if search string exists, if so return search results
        } else if (searchString != null) {
            try {
                bookListResponse.setBooks(bookDAO.searchBooks(searchString));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title not found");
                return;
            }
            // finally else respond with page of books
        } else {
            bookListResponse.setBooks(bookDAO.getPageOfBooks(1000 + (pageSize * (pageNumber-1)), pageSize));
        }

        // set response values
        response.setContentType(format);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);

        // set custom header to number of total pages for client pagination needs
        response.setHeader("X-Total-Pages", String.valueOf(bookDAO.getNumberOfPages(pageSize)));

        // print books to response in specified format using responseWriter object.
        out.write(responseWriter.write(bookListResponse, format));
    }

    // post request handler
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        InputStream inputStream;

        PrintWriter out = response.getWriter();

        // get format of received data
        String contentType = request.getHeader("Content-Type");

        try {
            inputStream = request.getInputStream();

            // parse input stream as a string
            String requestData = inputStreamUtils.getStringFromInputStream(inputStream);

            // convert string into bookList object using requestReader object
            BookList books = requestReader.read(requestData, contentType);

            // add received books to database
            for (Book book : books.getBooks()) {
                bookDAO.addBook(book);
            }

        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formatting issue");
            return;
        }

        out.write("Book added");
        response.setStatus(201);
    }

    // delete request handler
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // get id of book to delete
        String idParam = request.getParameter("id");

        // use DAO to delete book and return whether it was deleted
        boolean bookDeleted = bookDAO.deleteBook(Integer.parseInt(idParam));
        PrintWriter out = response.getWriter();

        // respond with error or success message
        if (bookDeleted) {
            out.write("Book with id " + idParam + " deleted.");
            response.setStatus(200);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No book exists of id " + idParam);
        }
    }

    // put response handler
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        InputStream inputStream;

        // get format of received data
        String contentType = request.getHeader("Content-Type");

        try {
            inputStream = request.getInputStream();

            // parse input stream as string
            String requestData = inputStreamUtils.getStringFromInputStream(inputStream);

            // convert string to bookList object using requestReader object
            BookList books = requestReader.read(requestData, contentType);

            // update edited books using DAO
            for (Book book : books.getBooks()) {
                bookDAO.updateBook(book);
            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No book exists");
        }

        out.write("Book was updated.");
        response.setStatus(200);
    }
}
