package com.system.controller.restaurant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotBlank;

public class RestaurantInput {

    @NotBlank
    private final String name;

    @JsonCreator
    public RestaurantInput(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
