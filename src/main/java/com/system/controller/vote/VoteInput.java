package com.system.controller.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

@SuppressWarnings("CanBeFinal")
public class VoteInput {

    private final URI restaurantUri;

    public URI getRestaurantUri() {
        return restaurantUri;
    }

    @JsonCreator
    public VoteInput(@JsonProperty("restaurant") final URI restaurantUri) {
        this.restaurantUri = restaurantUri;
    }
}