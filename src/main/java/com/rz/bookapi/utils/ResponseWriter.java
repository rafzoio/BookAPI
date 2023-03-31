package com.rz.bookapi.utils;

import com.google.gson.Gson;
import com.rz.bookapi.model.BookList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class ResponseWriter {

    private final Gson gson;

    private final JAXBContext jaxbContext;

    public ResponseWriter() {
        this.gson = new Gson();
        try {
            this.jaxbContext = JAXBContext.newInstance(BookList.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public String print(BookList allBooks, String format) {
        if (format.equals("application/json")) {
            return gson.toJson(allBooks);
        } else if (format.equals("application/xml")) {
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
        } else return allBooks.toString();
    }
}
