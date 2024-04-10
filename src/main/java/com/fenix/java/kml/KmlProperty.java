package com.fenix.java.kml;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class KmlProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id = "0";

    private Set<String> ids = new HashSet<>();

    private String name;

    private String description;

    private List<Coordinate> points = new ArrayList<>();

    //0：点     1：线     2：面
    private Integer type;

    private List<KmlProperty> kmlPropertyList = new ArrayList<>();



}