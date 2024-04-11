package jav.fenix.kml;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class KmlData {

    @ExcelProperty("文件路径")
    private String filePath;
    @ExcelProperty("文件名")
    private String fileName;

    @ExcelProperty("Document名")
    private String documentName;
    @ExcelProperty("Folder名")
    private String folderName;
    @ExcelProperty("placement名")
    private String placemarkName;

    @ExcelProperty("坐标最东")
    private String east;
    @ExcelProperty("坐标最西")
    private String west;
    @ExcelProperty("坐标最南")
    private String south;
    @ExcelProperty("坐标最北")
    private String north;

    @ExcelProperty("覆盖面积")
    private String area;
    @ExcelProperty("覆盖区域")
    private String region;
}
