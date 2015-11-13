package com.system.controller.menu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.system.domain.Dish;

import java.net.URI;
import java.util.Set;

import javax.validation.constraints.Size;

public class MenuPatchInput {

    @Size(min = 2, max = 5)
    private final
    Set<Dish> dishes;

    private final URI restaurantUri;

    public URI getRestaurantUri() {
        return restaurantUri;
    }


    public Set<Dish> getDishes() {
        return dishes;
    }


    @JsonCreator
    public MenuPatchInput(@JsonProperty("dishes") final Set<Dish> dishes,
                          @JsonProperty("restaurantUri") final URI restaurantUri) {
        this.dishes = dishes;
        this.restaurantUri = restaurantUri;
    }
}