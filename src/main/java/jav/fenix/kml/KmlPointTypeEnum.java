package jav.fenix.kml;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KmlPointTypeEnum {
    //kml文件数据类型
    POINT(0, "点"),
    LINE(1, "线"),
    POLYGON(2, "面");

    private final Integer code;
    private final String name;
}
