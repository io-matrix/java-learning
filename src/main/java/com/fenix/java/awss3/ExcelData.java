package com.fenix.java.awss3;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelData {

    @ExcelProperty("序号")
    private String id;
    @ExcelProperty("目录")
    private String path;
    @ExcelProperty("对象数")
    private long count;
    @ExcelProperty("xsky对象数")
    private long xskyCount;
    @ExcelProperty("xsky多的对象数")
    private long diffCount;
    @ExcelProperty("key编码对象数")
    private long encodeCodeCount;

}
