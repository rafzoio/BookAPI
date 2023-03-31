package com.rz.bookapi.utils;

import com.google.gson.Gson;
import com.rz.bookapi.model.BookList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class RequestReader {

    private final Gson gson;

    private final JAXBContext jaxbContext;

    public RequestReader() {
        this.gson = new Gson();
        try {
            this.jaxbContext = JAXBContext.newInstance(BookList.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public BookList read(String requestData, String contentType) {
        if (contentType.equals("application/xml")) {
            try {
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                return (BookList) unmarshaller.unmarshal(new StringReader(requestData));
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        } else if (contentType.equals("application/json")) {
            return gson.fromJson(requestData, BookList.class);
        } else {
            return null;
        }
    }
}
