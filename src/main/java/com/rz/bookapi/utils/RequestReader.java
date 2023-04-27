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

    private RequestReader() {
        this.gson = new Gson();
        try {
            this.jaxbContext = JAXBContext.newInstance(BookList.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static RequestReader getInstance() {
        if (instance == null) {
            instance = new RequestReader();
        }
        return instance;
    }

    public BookList read(String requestData, String contentType) {
        if (contentType.equals("application/xml")) {
            try {
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return (BookList) unmarshaller.unmarshal(new StringReader(requestData));
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        } else if (contentType.equals("text/plain")) {
            return delimitedStringToBook(requestData);
        } else {
            return gson.fromJson(requestData, BookList.class);
        }
    }

    private BookList delimitedStringToBook(String inputString) {
        String[] lines = inputString.split("\n");
        BookList bookList = new BookList();
        List<Book> newBooks = new ArrayList<>();
        for (String line : lines) {
            String[] props = line.split("#");
            Book newBook = new Book(
                    props[0],
                    props[1],
                    props[2],
                    props[3],
                    props[4],
                    props[5]
            );
            newBooks.add(newBook);
        }
        bookList.setBooks(newBooks);
        return bookList;
    }
}
