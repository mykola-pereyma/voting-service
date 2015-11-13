package com.system.controller.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotBlank;

import java.net.URI;

public class VotePatchInput {

    private URI restaurantUri;

    public URI getRestaurantUri() {
        return restaurantUri;
    }

    @JsonCreator
    public VotePatchInput(@JsonProperty("restaurant") final URI restaurantUri) {
        this.restaurantUri = restaurantUri;
    }
}