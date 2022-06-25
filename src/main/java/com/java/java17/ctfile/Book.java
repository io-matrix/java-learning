package com.java.java17.ctfile;

public class Book {
    private String bookName;
    private String bookContent;
    private String bookFileSize;
    private String bookYear;
    private String bookFormat;
    private String bookAuthor;
    private String bookShareUrl;
    private String bookSharePwd;
    private String bookShareUrl2;
    private String bookSharePwd2;
    private String bookZipPwd;
    private String coverPath;


    public Book(String bookName, String bookContent, String bookFileSize, String bookYear, String bookFormat, String bookAuthor, String bookShareUrl, String bookSharePwd, String bookShareUrl2, String bookSharePwd2, String bookZipPwd, String coverPath) {
        this.bookName = bookName;
        this.bookContent = bookContent;
        this.bookFileSize = bookFileSize;
        this.bookYear = bookYear;
        this.bookFormat = bookFormat;
        this.bookAuthor = bookAuthor;
        this.bookShareUrl = bookShareUrl;
        this.bookSharePwd = bookSharePwd;
        this.bookShareUrl2 = bookShareUrl2;
        this.bookSharePwd2 = bookSharePwd2;
        this.bookZipPwd = bookZipPwd;
        this.coverPath = coverPath;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookContent() {
        return bookContent;
    }

    public void setBookContent(String bookContent) {
        this.bookContent = bookContent;
    }

    public String getBookFileSize() {
        return bookFileSize;
    }

    public void setBookFileSize(String bookFileSize) {
        this.bookFileSize = bookFileSize;
    }

    public String getBookYear() {
        return bookYear;
    }

    public void setBookYear(String bookYear) {
        this.bookYear = bookYear;
    }

    public String getBookFormat() {
        return bookFormat;
    }

    public void setBookFormat(String bookFormat) {
        this.bookFormat = bookFormat;
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

    public String getBookShareUrl2() {
        return bookShareUrl2;
    }

    public void setBookShareUrl2(String bookShareUrl2) {
        this.bookShareUrl2 = bookShareUrl2;
    }

    public String getBookSharePwd2() {
        return bookSharePwd2;
    }

    public void setBookSharePwd2(String bookSharePwd2) {
        this.bookSharePwd2 = bookSharePwd2;
    }

    public String getBookZipPwd() {
        return bookZipPwd;
    }

    public void setBookZipPwd(String bookZipPwd) {
        this.bookZipPwd = bookZipPwd;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
