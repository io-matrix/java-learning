package jav.fenix.kml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class BaiduRegion {

    @JsonProperty("status")
    private Integer status;
    @JsonProperty("result")
    private ResultDTO result;

    @NoArgsConstructor
    @Data
    public static class ResultDTO {
        @JsonProperty("location")
        private LocationDTO location;
        @JsonProperty("formatted_address")
        private String formattedAddress;
        @JsonProperty("edz")
        private EdzDTO edz;
        @JsonProperty("business")
        private String business;
        @JsonProperty("addressComponent")
        private AddressComponentDTO addressComponent;
        @JsonProperty("pois")
        private List<?> pois;
        @JsonProperty("roads")
        private List<?> roads;
        @JsonProperty("poiRegions")
        private List<?> poiRegions;
        @JsonProperty("sematic_description")
        private String sematicDescription;
        @JsonProperty("formatted_address_poi")
        private String formattedAddressPoi;
        @JsonProperty("cityCode")
        private Integer cityCode;

        @NoArgsConstructor
        @Data
        public static class LocationDTO {
            @JsonProperty("lng")
            private Double lng;
            @JsonProperty("lat")
            private Double lat;
        }

        @NoArgsConstructor
        @Data
        public static class EdzDTO {
            @JsonProperty("name")
            private String name;
        }

        @NoArgsConstructor
        @Data
        public static class AddressComponentDTO {
            @JsonProperty("country")
            private String country;
            @JsonProperty("country_code")
            private Integer countryCode;
            @JsonProperty("country_code_iso")
            private String countryCodeIso;
            @JsonProperty("country_code_iso2")
            private String countryCodeIso2;
            @JsonProperty("province")
            private String province;
            @JsonProperty("city")
            private String city;
            @JsonProperty("city_level")
            private Integer cityLevel;
            @JsonProperty("district")
            private String district;
            @JsonProperty("town")
            private String town;
            @JsonProperty("town_code")
            private String townCode;
            @JsonProperty("distance")
            private String distance;
            @JsonProperty("direction")
            private String direction;
            @JsonProperty("adcode")
            private String adcode;
            @JsonProperty("street")
            private String street;
            @JsonProperty("street_number")
            private String streetNumber;
        }
    }
}
