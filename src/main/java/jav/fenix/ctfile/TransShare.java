package jav.fenix.ctfile;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TransShare {
    public static void main(String[] args) {

        String shareName = "share_name";
        String shareUrl = "share_url";
        String sharePwd = "share_pwd";

        List<String> shareStringList = FileUtil.readLines("/Users/feng/book/share.json", StandardCharsets.UTF_8);

        Map<String, Map<String, String>> shareMap = new HashMap<>();

        for (String share : shareStringList) {
            Map<String, Object> map = JSON.parseObject(share, HashMap.class);

            JSONArray jsonArray = (JSONArray) map.get("items");

            for (Object iterm : jsonArray) {
                Map oMap = (Map) iterm;
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put(shareName, (String) oMap.get(shareName));
                resultMap.put(shareUrl, (String) oMap.get(shareUrl));
                resultMap.put(sharePwd, (String) oMap.get(sharePwd));
                shareMap.put((String) oMap.get(shareName), resultMap);
            }


        }

        List<String> bookStringList = FileUtil.readLines("/Users/feng/book/select.json", StandardCharsets.UTF_8);

        int count = 102906;
        for (String bookString : bookStringList) {
            count++;
            log.info("序号： {}", count);
            Book book = JSON.parseObject(bookString, Book.class);
            try {
                Map<String, String> bookMap = shareMap.get(String.valueOf(count));
                book.setBookShareUrl(bookMap.get(shareUrl));
                book.setBookSharePwd(bookMap.get(sharePwd));
                book.setBookZipPwd("");
            } catch (Exception e) {
                FileUtil.appendString(JSON.toJSONString(book) + "\n", "/Users/feng/book/error.json", StandardCharsets.UTF_8);
                log.info("序号：{} {} 失败", count, book.getBookName());
                continue;
            }
            FileUtil.appendString(JSON.toJSONString(book) + "\n", "/Users/feng/book/update.json", StandardCharsets.UTF_8);

        }
    }
}
