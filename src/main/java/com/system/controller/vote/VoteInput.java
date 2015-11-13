package com.system.controller.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotBlank;

import java.net.URI;

public class VoteInput {

    private URI restaurantUri;

    public URI getRestaurantUri() {
        return restaurantUri;
    }

    @JsonCreator
    public VoteInput(@JsonProperty("restaurant") final URI restaurantUri) {
        this.restaurantUri = restaurantUri;
    }
}