package com.system.controller.restaurant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotBlank;

public class RestaurantPatchInput {

    @NotBlank
    private final String name;

    @JsonCreator
    public RestaurantPatchInput(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
