package com.fenix.java.kml;


import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class KmlPoint implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Coordinate> points = new ArrayList<>();
    private String name;
    private String description;

    //0：点     1：线     2：面
    private Integer type;

}
