package com.rz.bookapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "book")
@XmlType(propOrder = {"id", "title", "author", "date", "genres", "characters", "synopsis"})

public class Book {

    private int id;
    private String title;
    private String author;
    private String date;
    private String genres;
    private String characters;
    private String synopsis;

    public Book(String title, String author, String date, String genres, String characters, String synopsis) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.genres = genres;
        this.characters = characters;
        this.synopsis = synopsis;
    }
}
