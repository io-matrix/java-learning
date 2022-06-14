package com.java.java17.awss3;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelData {

    @ExcelProperty("序号")
    private String id;
    @ExcelProperty("目录")
    private String path;
    @ExcelProperty("对象数")
    private String count;
    @ExcelProperty("xsky对象数")
    private String xskyCount;

}
