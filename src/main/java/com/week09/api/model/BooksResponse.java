package com.week09.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * BooksResponse – wrapper POJO for the BookStore /Books endpoint.
 *
 * Actual JSON shape:
 * {
 *   "books": [
 *     { "isbn": "...", "title": "...", ... },
 *     ...
 *   ]
 * }
 *
 * This class is the top-level object Jackson deserialises into.
 * The List<Book> is accessed via getBooks().
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BooksResponse {

    private List<Book> books;

    public List<Book> getBooks() { return books; }

    public void setBooks(List<Book> books) { this.books = books; }
}
