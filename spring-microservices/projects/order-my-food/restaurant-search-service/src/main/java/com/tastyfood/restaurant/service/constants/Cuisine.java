package com.tastyfood.restaurant.service.constants;

import org.springframework.util.StringUtils;

public enum Cuisine {

    MULTI_CUISINE, SOUTH_INDIAN, NORTH_INDIAN, ITALIAN, CHINESE;

    public static Cuisine getEnum(String cuisineName) {
        if (StringUtils.hasText(cuisineName)) {
            return Cuisine.valueOf(cuisineName.toUpperCase().replace(' ', '_'));
        } else {
            return MULTI_CUISINE;
        }
    }

}
