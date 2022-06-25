package com.java.java17.ctfile;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class TransFile {

    public static void main(String[] args) throws Exception {

        String prefix = "https://zlib.cc/prod-api";

        String bookJson = GetBook.readJsonFile("D:\\book\\books.json");

        List<Book> bookList = JSONArray.parseArray(bookJson, Book.class);

        for (Book book : bookList) {
            book.setCoverPath(prefix + book.getCoverPath());
            book.setBookShareUrl(book.getBookShareUrl2());
            book.setBookSharePwd(book.getBookSharePwd2());
            book.setBookShareUrl2(null);
            book.setBookSharePwd2(null);
            if (StrUtil.isEmpty(book.getBookAuthor())) {
                book.setBookAuthor("无名");
            }
            FileUtil.appendString(JSON.toJSONString(book) + "\n", "D:\\book\\books2.json", StandardCharsets.UTF_8);
        }


    }

}
