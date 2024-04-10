package com.fenix.java.kml;


import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description 根据经纬度计算多边形面积（墨卡托投影）
 * @Date 2022-05-18 15:06
 * @Author xie
 */
public class CalculateArea {


    /**
     * 地球半径（米）
     * 6371000.0 ~ 6371009.0
     */
    private static double earthRadiusMeters = 6371006.0;

    /**
     * 米每度
     */
    private static double metersPerDegree = 2.0 * Math.PI * earthRadiusMeters / 360.0;

    /**
     * 弧度每度
     */
    private static double radiansPerDegree = Math.PI / 180.0;

    /**
     * 度每弧度
     */
    private static double degreesPerRadian = 180.0 / Math.PI;

    public static void main(String[] args) {

        //LatLng point1 = new LatLng(108.9423179626465, 34.271545240616625);
        //LatLng point2 = new LatLng(108.95819664001465, 34.271474311989216);
        //LatLng point3 = new LatLng(108.9582395553589, 34.26594169471089);
        //LatLng point4 = new LatLng(108.94257545471193, 34.265906228041345);

        LatLng point1 = new LatLng(23.27933419, 113.09040785);
        LatLng point2 = new LatLng(23.27969884, 113.10045004);
        LatLng point3 = new LatLng(23.26384062, 113.10378671);
        LatLng point4 = new LatLng(23.26328865, 113.09379816);
        LatLng point5 = new LatLng(23.27933419, 113.09040785);

        List<LatLng> pointList = Stream.of(point1, point2, point3, point4, point5).collect(Collectors.toList());

        System.out.println("球面多边形：" + calculateArea(pointList));

        System.out.println("平面多边形：" + planarPolygonAreaMeters2(pointList));

    }

    public static double calculateArea(List<LatLng> pointList) {
        if (CollectionUtils.isNotEmpty(pointList) && pointList.size() > 2) {
            // 平面多边形面积
            double areaMeters2 = planarPolygonAreaMeters2(pointList);
            if (areaMeters2 > 1000000.0) {
                // 球面多边形面积计算
                areaMeters2 = sphericalPolygonAreaMeters2(pointList);
            }
            return areaMeters2;
        }
        return 0D;
    }

    /**
     * 球面多边形面积计算
     * @return double
     */
    public static double sphericalPolygonAreaMeters2(List<LatLng> pointList) {
        // 总角度
        double totalAngle = 0;
        int size = pointList.size();
        for (int i = 0; i < pointList.size(); i++) {
            int j = (i + 1) % size;
            int k = (i + 2) % size;
            totalAngle += angle(pointList.get(i), pointList.get(j), pointList.get(k));
        }

        // 平面总角度
        double planarTotalAngle = (size - 2) * 180.0;

        // 球形过剩
        double sphericalExcess = totalAngle - planarTotalAngle;
        if (sphericalExcess > 420.0) {
            totalAngle = size * 360.0 - totalAngle;
            sphericalExcess = totalAngle - planarTotalAngle;
        } else if (sphericalExcess > 300.0 && sphericalExcess < 420.0) {
            sphericalExcess = Math.abs(360.0 - sphericalExcess);
        }

        return sphericalExcess * radiansPerDegree * earthRadiusMeters * earthRadiusMeters;
    }

    /**
     * 角度
     * @return double
     */
    public static double angle(LatLng point1, LatLng point2, LatLng point3) {

        double bearing21 = bearing(point2, point1);
        double bearing23 = bearing(point2, point3);
        double angle = bearing21 - bearing23;
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    /**
     * 方向
     */
    public static double bearing(LatLng from, LatLng to) {
        double lat1 =from.getLat() * radiansPerDegree;
        double lon1 = from.getLon() * radiansPerDegree;
        double lat2 = to.getLat() * radiansPerDegree;
        double lon2 = to.getLon() * radiansPerDegree;
        double angle = -Math.atan2(
                Math.sin(lon1 - lon2) * Math.cos(lat2),
                Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        if (angle < 0) {
            angle += Math.PI * 2.0;
        }
        angle = angle * degreesPerRadian;

        return angle;
    }


    /**
     * 平面多边形面积
     * @return double
     */
    public static double planarPolygonAreaMeters2(List<LatLng> pointList) {
        double a = 0;
        for (int i = 0; i < pointList.size(); i++) {
            int j = (i + 1) % pointList.size();

            LatLng point_i = pointList.get(i);
            double xi = point_i.getLon() * metersPerDegree * Math.cos(point_i.getLat() * radiansPerDegree);
            double yi = point_i.getLat() * metersPerDegree;

            LatLng point_j = pointList.get(j);
            double xj = point_j.getLon() * metersPerDegree * Math.cos(point_j.getLat() * radiansPerDegree);
            double yj = point_j.getLat() * metersPerDegree;

            a += xi * yj - xj * yi;
        }
        // 转化为正数
        return Math.abs(a / 2);
    }
}
