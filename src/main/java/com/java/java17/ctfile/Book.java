package com.java.java17.ctfile;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Date;

public class Book {
    @JSONField(ordinal = 2)
    private String _id;
    @JSONField(ordinal = 1)
    private String bookName;


    @JSONField(ordinal = 20)
    private String bookContent;
    @JSONField(ordinal = 2)
    private String bookFileSize;
    @JSONField(ordinal = 20)
    private String bookYear;
    @JSONField(ordinal = 20)
    private String bookFormat;
    @JSONField(ordinal = 3)
    private String bookAuthor;
    @JSONField(ordinal = 20)
    private String bookShareUrl;
    @JSONField(ordinal = 20)
    private String bookSharePwd;
    @JSONField(ordinal = 20)
    private String bookShareUrl2;
    @JSONField(ordinal = 20)
    private String bookSharePwd2;
    @JSONField(ordinal = 20)
    private String bookZipPwd;
    @JSONField(ordinal = 20)
    private String coverPath;
    @JSONField(ordinal = 20)
    private Date updateTime;


    public Book(String bookName, String bookContent, String bookFileSize, String bookYear, String bookFormat, String bookAuthor, String bookShareUrl, String bookSharePwd, String bookShareUrl2, String bookSharePwd2, String bookZipPwd, String coverPath, Date updateTime) {
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
        this.updateTime = updateTime;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
