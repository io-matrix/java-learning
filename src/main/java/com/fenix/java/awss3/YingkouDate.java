package com.fenix.java.awss3;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author feng
 * @desc 描述
 * @date 2022/10/12 11:11
 * @since v1
 */
@Data
public class YingkouDate {


    @ExcelProperty("文件key")
    private String key;
    @ExcelProperty("大小")
    private String size;
    @ExcelProperty("上传时间")
    private String date;
    @ExcelProperty("所属桶")
    private String bucket;


}
