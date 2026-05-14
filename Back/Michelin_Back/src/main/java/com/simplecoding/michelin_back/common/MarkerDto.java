package com.simplecoding.michelin_back.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkerDto {

    private Long id;
    private String restaurantName;
    private Double lat;
    private Double lng;
    private String grade;
    private String phone;
    private String address;
    private String markerColor;
}