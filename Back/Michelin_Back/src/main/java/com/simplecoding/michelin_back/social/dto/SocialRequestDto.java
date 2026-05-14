package com.simplecoding.michelin_back.social.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialRequestDto {
    private Long userId;
    private Long restaurantId;
}
