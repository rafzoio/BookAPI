package com.rz.bookapi.utils;

import com.google.gson.Gson;
import com.rz.bookapi.model.Book;
import com.rz.bookapi.model.BookList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RequestReader {

    private static RequestReader instance;
    private final Gson gson;
    private final JAXBContext jaxbContext;

    // initialise gson and jaxb dependencies
    private RequestReader() {
        this.gson = new Gson();
        try {
            // set jaxb context to be of BookList.class type
            this.jaxbContext = JAXBContext.newInstance(BookList.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    // get instance method to initialise singleton
    public static RequestReader getInstance() {
        if (instance == null) {
            instance = new RequestReader();
        }
        return instance;
    }

    /**
     * Read method to generate a BookList object from specified format
     *
     * @param requestData book data from put or post request
     * @param contentType format of submitted data
     * @return BookList object containing Book objects
     */
    public BookList read(String requestData, String contentType, String requestType) {
        // check if xml, then use jaxb to unmarshall xml
        if (contentType.equals("application/xml")) {
            try {
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return (BookList) unmarshaller.unmarshal(new StringReader(requestData));
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
            // check if text/plain, then use delimitedStringToBookMethod
        } else if (contentType.equals("text/plain")) {
            return delimitedStringToBook(requestData, requestType);
            // else treat as JSON and use GSON to parse data
        } else {
            return gson.fromJson(requestData, BookList.class);
        }
    }

    /**
     * Converts delimited string into BookList object
     *
     * @param inputString raw data in text/plain format
     * @return BookList of all books contained in data
     */
    private BookList delimitedStringToBook(String inputString, String requestType) {
        String[] lines = inputString.split("\n");
        BookList bookList = new BookList();
        List<Book> newBooks = new ArrayList<>();
        for (String line : lines) {
            String[] props = line.split("#", -1);
            Book newBook;
            if (requestType.equals("put")) {
                newBook = new Book(
                        Integer.parseInt(props[0]),
                        props[1],
                        props[2],
                        props[3],
                        props[4],
                        props[5],
                        props[6]
                );
            } else {
                newBook = new Book(
                        props[0],
                        props[1],
                        props[2],
                        props[3],
                        props[4],
                        props[5]
                );
            }

            newBooks.add(newBook);
        }
        bookList.setBooks(newBooks);
        return bookList;
    }
}
