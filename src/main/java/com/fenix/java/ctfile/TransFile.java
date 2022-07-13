package com.fenix.java.ctfile;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.java.java17.utils.DownLoadUtil;
import com.java.java17.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class TransFile {

    private static final String SRC_PATH = "/Users/feng/book/images/";
    private static final String DEST_PATH = "/Users/feng/book/covers/";
    static SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();
    static String COVER_PREFIX = "https://zlib.cc/prod-api";
    static String FINAL_COVER_PREFIX = "https://ssc-static-objects.obs.cn-southwest-2.myhuaweicloud.com/images/h5/book/";

    /**
     * 转换books2.json 根据书名排序
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        selectBook();
    }

    public static void selectBook() {
        List<String> shudanList = FileUtil.readLines("/Users/feng/book/shudan.log", StandardCharsets.UTF_8);
        List<String> bookStringList = FileUtil.readLines("/Users/feng/book/yun_data.json", StandardCharsets.UTF_8);

        List<String> nameList = new ArrayList<>();
//        for (String book : shudanList) {
//            String substring = book.substring(book.indexOf("《")+1, book.indexOf("》"));
//            log.info(substring);
//            nameList.add(substring);
//        }


        List<Book> bookList = new ArrayList<>();
        for (String bookString : bookStringList) {
            Book book = JSON.parseObject(bookString, Book.class);
            if (book.getBookShareUrl().contains("aliyun")) {
                continue;
            }
            bookList.add(book);
//            for (String name : nameList) {
//                if (StrUtil.isNotEmpty(name) && book.getBookName().toLowerCase().contains(name.trim().toLowerCase())) {
//
//                }
//            }
        }


        for (Book book : bookList) {
            FileUtil.appendString(JSON.toJSONString(book) + "\n", "/Users/feng/book/select.json", StandardCharsets.UTF_8);
        }
    }

    public static void sortByBookName() {
        List<String> bookStringList = FileUtil.readLines("/Users/feng/book/books2.json", StandardCharsets.UTF_8);

        List<Book> bookList = new ArrayList<>();
        for (String bookString : bookStringList) {
            Book book = JSON.parseObject(bookString, Book.class);
            bookList.add(book);
        }

        Collections.sort(bookList, Comparator.comparing(Book::getBookName).thenComparing(Book::getBookFileSize));

        Map<String, Book> map = new HashMap<>();

        for (Book book : bookList) {
            map.put(book.getBookName(), book);
        }
        Collection<Book> values = map.values();

        List<Book> finalBook = new ArrayList<>();
        List<Book> finalBookList = new ArrayList<>();
        for (Book value : values) {
            finalBook.add(value);
        }

        Collections.sort(finalBook, Comparator.comparing(Book::getBookName).thenComparing(Book::getBookFileSize));

        for (int i = 0; i < finalBook.size(); i++) {
            Book book = finalBook.get(i);
            if (i == finalBook.size() - 1) {
                finalBookList.add(book);
                break;
            }


            Book nextBook = finalBook.get(i + 1);

            if (!nextBook.getBookName().contains(book.getBookName())) {
                finalBookList.add(book);
            } else {
                if (nextBook.getBookFormat().contains(book.getBookFormat())
                        && Double.parseDouble(nextBook.getBookFileSize()) > Double.parseDouble(book.getBookFileSize())) {
                    continue;
                }
                finalBookList.add(book);
            }
        }

        for (Book book : finalBookList) {
            FileUtil.appendString(JSON.toJSONString(book) + "\n", "/Users/feng/book/booksSort.json", StandardCharsets.UTF_8);
        }
    }

    /**
     * 转换zlib 原数据
     *
     * @throws Exception
     */
    public static void trans1() throws Exception {
        String bookJson = GetBook.readJsonFile("/Users/feng/book/books.json");

        List<Book> bookList = JSONArray.parseArray(bookJson, Book.class);

        for (int i = 0; i < bookList.size(); i++) {
            Book book = bookList.get(i);
            log.info("{}. {}", i, book.getBookName());

            String key = generateUUID() + ".jpg";
            String coverImageUrl = COVER_PREFIX + book.getCoverPath();
            String[] split = book.getCoverPath().split("/");
            String image = split[split.length - 1];
            String srcFile = SRC_PATH + image;
            String destFile = DEST_PATH + key;

            try {
                DownLoadUtil.downloadFile(coverImageUrl, SRC_PATH, image);
            } catch (Exception e) {
                log.error("{} 失败！", book.getBookName(), e);
                continue;
            }
            ImageUtil.reduceImg(srcFile, destFile);

            book.setCoverPath(FINAL_COVER_PREFIX + key);
            book.setBookShareUrl(book.getBookShareUrl2().split("\\?")[0]);
            book.setBookSharePwd(book.getBookSharePwd2());
            book.setBookShareUrl2(null);
            book.setBookSharePwd2(null);
            book.setUpdateTime(new Date());
            if (StrUtil.isEmpty(book.getBookAuthor())) {
                book.setBookAuthor("无名");
            }
            FileUtil.appendString(JSON.toJSONString(book) + "\n", "/Users/feng/book/books2.json", StandardCharsets.UTF_8);
            FileUtil.del(srcFile);
            log.info("成功：{}", book.getBookName());
        }
    }

    public static String generateSnow() {
        return snowflakeGenerator.next().toString();
    }

    public static String generateUUID() {
        return UUID.randomUUID(true).toString(true);
    }

}
