package com.rz.bookapi.utils;

import com.google.gson.Gson;
import com.rz.bookapi.model.Book;
import com.rz.bookapi.model.BookList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;

public class ResponseWriter {

    private final Gson gson;

    private final JAXBContext jaxbContext;

    private static ResponseWriter instance;

    // initialise gson and jaxb dependencies
    private ResponseWriter() {
        this.gson = new Gson();
        try {
            // set jaxb context to be of BookList.class type
            this.jaxbContext = JAXBContext.newInstance(BookList.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    // get instance method to initialise singleton
    public static ResponseWriter getInstance() {
        if (instance == null) {
            instance = new ResponseWriter();
        }
        return instance;
    }

    /**
     * Write method converts from a BookList class to string in specified format
     * @param allBooks Booklist object to convert
     * @param format requested format for response
     * @return string value containing data in specified format
     */
    public String write(BookList allBooks, String format) {
        // check xml, use jaxb to marshall data
        if (format.equals("application/xml")) {
            Marshaller m;
            StringWriter sw = new StringWriter();
            try {
                m = jaxbContext.createMarshaller();
                m.marshal(allBooks, sw);
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
            return sw.toString();
            // check text/plain, use bookToDelimitedString method to convert
        } else if (format.equals("text/plain")) {
            return bookToDelimitedString(allBooks);
            // by default use GSON to convert booklist to JSON
        } else {
            return gson.toJson(allBooks);
        }
    }

    /**
     * Generates a delimited string of books
     * @param books Booklist containing book objects
     * @return delimited string
     */
    private String bookToDelimitedString(BookList books) {
        StringBuilder builder = new StringBuilder();
        List<Book> allBooks = books.getBooks();
        for (Book allBook : allBooks) {
            builder.append(allBook.getId())
                    .append('#')
                    .append(allBook.getTitle())
                    .append('#')
                    .append(allBook.getAuthor())
                    .append('#')
                    .append(allBook.getDate())
                    .append('#')
                    .append(allBook.getGenres())
                    .append('#')
                    .append(allBook.getCharacters())
                    .append('#')
                    .append(allBook.getSynopsis())
                    .append("\n");
        }
        return builder.toString();
    }
}
