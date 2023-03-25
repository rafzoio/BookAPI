package com.rz.bookapi.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "bookList")
public class BookList {

    @XmlElement(name = "book")
    private List<Book> books;

    public BookList(List<Book> books) {
        this.books = books;
    }

    public BookList() {
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "BookList{" +
                "books=" + books +
                '}';
    }
}
