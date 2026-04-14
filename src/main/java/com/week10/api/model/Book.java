package com.week10.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Book – POJO representing a single book from the DemoQA BookStore API.
 *
 * Endpoint: GET https://demoqa.com/BookStore/v1/Books
 * Response: { "books": [ { ...Book fields... }, ... ] }
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) ensures that if the API adds
 * new fields in the future, Jackson does not throw an exception —
 * it simply ignores the unknown fields.
 *
 * Why no Lombok @Data here?
 *   We use explicit getters to keep the class readable as an educational
 *   example. In a real project you would add @Data @NoArgsConstructor.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    private String isbn;
    private String title;
    private String subTitle;
    private String author;

    @JsonProperty("publish_date")
    private String publishDate;

    private String publisher;
    private int    pages;
    private String description;
    private String website;

    // ── Getters ──────────────────────────────────────────────────────────

    public String getIsbn()        { return isbn; }
    public String getTitle()       { return title; }
    public String getSubTitle()    { return subTitle; }
    public String getAuthor()      { return author; }
    public String getPublishDate() { return publishDate; }
    public String getPublisher()   { return publisher; }
    public int    getPages()       { return pages; }
    public String getDescription() { return description; }
    public String getWebsite()     { return website; }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title + "', author='" + author + "'}";
    }
}
