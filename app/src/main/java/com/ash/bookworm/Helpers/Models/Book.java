package com.ash.bookworm.Helpers.Models;

public class Book {

    private String bookId;
    private String bookName;
    private String authorName;
    private String imageUrl;

    public Book() {
    }

    public Book(String bookId, String bookName, String authorName, String imageUrl) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
