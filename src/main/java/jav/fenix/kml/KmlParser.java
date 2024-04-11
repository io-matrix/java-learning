package jav.fenix.kml;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import de.micromata.opengis.kml.v_2_2_0.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@Slf4j
public class KmlParser {

    private final Snowflake snowflake = IdUtil.getSnowflake(1, 1);
    /**
     * 保存kml数据到临时表
     *
     * @param file 上传的文件实体
     * @return 自定义的KML文件实体
     */
    public static KmlProperty toData(File file) {
        Kml kml = Kml.unmarshal(file);

        if (kml == null) {
            throw new RuntimeException("处理失败，文件为 " + file.getPath());
        }

        Feature feature = kml.getFeature();

        KmlProperty kmlProperty = new KmlProperty();
        if(Objects.isNull(feature)){
            return kmlProperty;
        }
        kmlProperty.setName(feature.getName());
        KmlParser kmlParser = new KmlParser();
        kmlParser.parseFeature(feature, kmlProperty);
        return kmlProperty;
    }
    public static KmlProperty toData(InputStream content) {
        Kml kml = Kml.unmarshal(content);
        Feature feature = kml.getFeature();

        KmlProperty kmlProperty = new KmlProperty();
        if(Objects.isNull(feature)){
            return kmlProperty;
        }
        kmlProperty.setName(feature.getName());
        KmlParser kmlParser = new KmlParser();
        kmlParser.parseFeature(feature, kmlProperty);
        return kmlProperty;
    }

    private void parseFeature(Feature feature, KmlProperty kmlProperty) {
        if (feature instanceof Document) {
            List<Feature> featureList = ((Document) feature).getFeature();
            List<KmlProperty> kmlPropertyList = kmlProperty.getKmlPropertyList();
            featureList.forEach(d -> {
                if (d instanceof Placemark) {
                    getPlaceMark((Placemark) d, kmlProperty);
                } else {
                    KmlProperty kmlProperty1 = new KmlProperty();
                    kmlProperty1.setName(d.getName());
                    kmlProperty1.setId(snowflake.nextIdStr());
                    kmlPropertyList.add(kmlProperty1);
                    parseFeature(d, kmlProperty1);
                }
            });
        } else if (feature instanceof Folder) {
            List<Feature> featureList = ((Folder)feature).getFeature();
            List<KmlProperty> kmlPropertyList = kmlProperty.getKmlPropertyList();
            featureList.forEach(d -> {
                if (d instanceof Placemark) {
                    getPlaceMark((Placemark) d, kmlProperty);
                }else {
                    KmlProperty kmlProperty1 = new KmlProperty();
                    kmlProperty1.setName(d.getName());
                    kmlProperty1.setId(snowflake.nextIdStr());
                    kmlPropertyList.add(kmlProperty1);
                    parseFeature(d, kmlProperty1);
                }
            });
        }
    }

    private void getPlaceMark(Placemark placemark, KmlProperty kmlProperty) {
        Geometry geometry = placemark.getGeometry();
        String name = placemark.getName();
        String description = placemark.getDescription();
        parseGeometry(name, geometry, description, kmlProperty);
    }

    private void parseGeometry(String name, Geometry geometry, String description, KmlProperty kmlProperty) {
        if (geometry != null) {
            if (geometry instanceof Polygon) {
                Polygon polygon = (Polygon) geometry;
                Boundary outerBoundaryIs = polygon.getOuterBoundaryIs();
                if (outerBoundaryIs != null) {
                    LinearRing linearRing = outerBoundaryIs.getLinearRing();
                    if (linearRing != null) {
                        List<Coordinate> coordinates = linearRing.getCoordinates();
                        if (coordinates != null) {
                            outerBoundaryIs = ((Polygon) geometry).getOuterBoundaryIs();
                            addPolygonToList(name, outerBoundaryIs, description, kmlProperty);
                        }
                    }
                }
            } else if (geometry instanceof LineString) {
                LineString lineString = (LineString) geometry;
                List<Coordinate> coordinates = lineString.getCoordinates();
                if (coordinates != null) {
                    int width = 0;
                    coordinates = ((LineString) geometry).getCoordinates();
                    addLineStringToList(coordinates, name, description, kmlProperty);

                }
            } else if (geometry instanceof Point) {
                Point point = (Point) geometry;
                List<Coordinate> coordinates = point.getCoordinates();
                if (coordinates != null) {
                    coordinates = ((Point) geometry).getCoordinates();
                    addPointToList(coordinates, name, description, kmlProperty);
                }
            } else if (geometry instanceof MultiGeometry) {
                List<Geometry> geometries = ((MultiGeometry) geometry).getGeometry();
                for (Geometry geometryToMult : geometries) {
                    Boundary outerBoundaryIs;
                    List<Coordinate> coordinates;
                    if (geometryToMult instanceof Point) {
                        coordinates = ((Point) geometryToMult).getCoordinates();
                        addPointToList(coordinates, name, description, kmlProperty);
                    } else if (geometryToMult instanceof LineString) {
                        coordinates = ((LineString) geometryToMult).getCoordinates();
                        addLineStringToList(coordinates, name, description, kmlProperty);
                    } else if (geometryToMult instanceof Polygon) {
                        outerBoundaryIs = ((Polygon) geometryToMult).getOuterBoundaryIs();
                        addPolygonToList(name, outerBoundaryIs, description, kmlProperty);
                    }
                }
            }
        }
    }

    private void addPolygonToList(String name, Boundary outerBoundaryIs, String description, KmlProperty kmlProperty) {
        LinearRing linearRing = outerBoundaryIs.getLinearRing();//面
        List<Coordinate> coordinates = linearRing.getCoordinates();
        KmlProperty kmlProperty1 = new KmlProperty();
        kmlProperty1.setId(snowflake.nextIdStr());
        kmlProperty1.setName(name);
        kmlProperty1.setPoints(coordinates);
        kmlProperty1.setDescription(description);
        kmlProperty1.setType(KmlPointTypeEnum.POLYGON.getCode());
        List<KmlProperty> kmlPropertyList = kmlProperty.getKmlPropertyList();
        kmlPropertyList.add(kmlProperty1);
    }

    private void addLineStringToList(List<Coordinate> coordinates, String name, String description, KmlProperty kmlProperty) {
        KmlProperty kmlProperty1 = new KmlProperty();
        kmlProperty1.setId(snowflake.nextIdStr());
        kmlProperty1.setName(name);
        kmlProperty1.setPoints(coordinates);
        kmlProperty1.setDescription(description);
        kmlProperty1.setType(KmlPointTypeEnum.LINE.getCode());
        List<KmlProperty> kmlPropertyList = kmlProperty.getKmlPropertyList();
        kmlPropertyList.add(kmlProperty1);
    }

    private void addPointToList(List<Coordinate> coordinates, String name, String description, KmlProperty kmlProperty) {
        KmlProperty kmlProperty1 = new KmlProperty();
        kmlProperty1.setId(snowflake.nextIdStr());
        kmlProperty1.setName(name);
        kmlProperty1.setPoints(coordinates);
        kmlProperty1.setDescription(description);
        kmlProperty1.setType(KmlPointTypeEnum.POINT.getCode());
        List<KmlProperty> kmlPropertyList = kmlProperty.getKmlPropertyList();
        kmlPropertyList.add(kmlProperty1);
    }

}
