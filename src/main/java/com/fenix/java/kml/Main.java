package com.fenix.java.kml;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Main {


    public static final String ROOT_PATH = "D:\\Fenix\\Desktop\\TIE3YUAN";

    public static final String DOCUMENT_NAME = "";
    public static final String FOLDER_NAME = "";

    public static List<KmlData> kmlDataList = new ArrayList<>();
    public static void main(String[] args) throws IOException {

        parseKml(new File(ROOT_PATH));

        // 导出Excel
        EasyExcel.write("D:\\Fenix\\Desktop\\kml.xlsx", KmlData.class)
                .sheet()
                .doWrite(kmlDataList);

    }


    public static void parseKml(File file) {

        if (file.isDirectory()) {
            File[] subs = file.listFiles();
            for (File sub : subs) {
                parseKml(sub);
            }
        } else {
            String name = file.getName();
            log.info("当前处理文件：{}", file.getPath());
            String[] split = name.split("\\.");
            if (!split[split.length - 1].equals("kml")) {
                return;
            }
            KmlProperty kmlProperty = null;
            try {
                kmlProperty = KmlParser.toData(file);
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
            List<KmlProperty> kmlPropertyList = kmlProperty.getKmlPropertyList();
            if (CollectionUtils.isEmpty(kmlPropertyList)) {
                convertToExcel(kmlProperty, file);
            } else {
                Map<String, List<KmlProperty>> placemarkMap = kmlPropertyList.stream().collect(Collectors.groupingBy(KmlProperty::getName));

                convertToExcel(placemarkMap, file);
            }

        }

    }

    private static void convertToExcel(KmlProperty kmlProperty, File file) {
        String name = kmlProperty.getName();
        List<Coordinate> points = kmlProperty.getPoints();

        if (CollectionUtils.isEmpty(points)) {
            return;
        }

        KmlData kmlData = new KmlData();
        String path = file.getPath();
        kmlData.setFilePath(path.substring(path.indexOf("D:\\Fenix\\Desktop\\")));
        kmlData.setFileName(file.getName());

        kmlData.setDocumentName(DOCUMENT_NAME);
        kmlData.setFolderName(FOLDER_NAME);
        kmlData.setPlacemarkName(name);

        Map<String, String> coorMap = calCoordinate(points);
        kmlData.setEast(coorMap.get("EAST"));
        kmlData.setWest(coorMap.get("WEST"));
        kmlData.setSouth(coorMap.get("SOUTH"));
        kmlData.setNorth(coorMap.get("NORTH"));

        String area = calArea(points);
        kmlData.setArea(area);

        String region = calRegion(points);
        kmlData.setRegion(region);

        kmlDataList.add(kmlData);

    }

    private static String calArea(List<Coordinate> points) {

        List<LatLng> latLngList = new ArrayList<>();

        for (Coordinate point : points) {
            LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
            latLngList.add(latLng);
        }

        double v = CalculateArea.planarPolygonAreaMeters2(latLngList);

        return String.valueOf(v);
    }

    private static String calRegion(List<Coordinate> points) {
        Coordinate coordinate = points.get(0);
        String url = "https://api.map.baidu.com/reverse_geocoding/v3/?ak=tA4embbMChzrTBB9Hp3QZEBo05gPvm7o&output=json&coordtype=wgs84ll&location=" + coordinate.getLatitude() + "," + coordinate.getLongitude();

        String jsonString = HttpUtil.get(url);

        BaiduRegion baiduRegion = JSONUtil.toBean(jsonString, BaiduRegion.class);
        if (baiduRegion.getStatus().equals(0)) {
            String formattedAddress = baiduRegion.getResult().getFormattedAddress();
            return formattedAddress;
        }

        return "";
    }

    private static Map<String, String> calCoordinate(List<Coordinate> points) {

        String east = "180";
        String west = "0";
        String south = "90";
        String north = "0";

        for (Coordinate point : points) {
            // 经度
            double longitude = point.getLongitude();
            // 维度
            double latitude = point.getLatitude();

            east = String.valueOf(longitude < Double.valueOf(east) ? longitude : east);
            west = String.valueOf(longitude > Double.valueOf(west) ? longitude : west);

            south = String.valueOf(latitude < Double.valueOf(south) ? latitude : south);
            north = String.valueOf(latitude > Double.valueOf(north) ? latitude : north);
        }

        Map<String, String> result = new HashMap<>();

        result.put("EAST", east);
        result.put("WEST", west);
        result.put("SOUTH", south);
        result.put("NORTH", north);
        return result;
    }

    private static void convertToExcel(Map<String, List<KmlProperty>> placemarkMap, File file) {

        placemarkMap.forEach((key, value) -> {
            KmlProperty kmlProperty = new KmlProperty();
            List<Coordinate> coordinateList = new ArrayList<>();
            for (KmlProperty property : value) {
                List<KmlProperty> kmlPropertyList = property.getKmlPropertyList();
                if (CollectionUtils.isNotEmpty(property.getPoints())) {
                    coordinateList.addAll(property.getPoints());
                }
                if (CollectionUtils.isNotEmpty(kmlPropertyList)) {
                    handleMultiFolder(kmlPropertyList, coordinateList);
                }
            }
            kmlProperty.setName(key);
            kmlProperty.setPoints(coordinateList);
            convertToExcel(kmlProperty, file);
        });
    }

    private static void handleMultiFolder(List<KmlProperty> kmlPropertyList, List<Coordinate> coordinateList) {
        for (KmlProperty kmlProperty : kmlPropertyList) {
            if (CollectionUtils.isNotEmpty(kmlProperty.getPoints())) {
                coordinateList.addAll(kmlProperty.getPoints());
            }
            if (CollectionUtils.isNotEmpty(kmlProperty.getKmlPropertyList())) {
                handleMultiFolder(kmlProperty.getKmlPropertyList(), coordinateList);
            }
        }
    }

}
