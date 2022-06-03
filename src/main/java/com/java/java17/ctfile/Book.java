package com.java.java17.ctfile;

public class Book {
    private String bookName;
    private String bookAuthor;
    private String bookShareUrl;
    private String bookSharePwd;
    private String bookZipPwd;

    public Book(String bookName, String bookAuthor, String bookShareUrl, String bookSharePwd, String bookZipPwd) {
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookShareUrl = bookShareUrl;
        this.bookSharePwd = bookSharePwd;
        this.bookZipPwd = bookZipPwd;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookShareUrl() {
        return bookShareUrl;
    }

    public void setBookShareUrl(String bookShareUrl) {
        this.bookShareUrl = bookShareUrl;
    }

    public String getBookSharePwd() {
        return bookSharePwd;
    }

    public void setBookSharePwd(String bookSharePwd) {
        this.bookSharePwd = bookSharePwd;
    }

    public String getBookZipPwd() {
        return bookZipPwd;
    }

    public void setBookZipPwd(String bookZipPwd) {
        this.bookZipPwd = bookZipPwd;
    }
}
