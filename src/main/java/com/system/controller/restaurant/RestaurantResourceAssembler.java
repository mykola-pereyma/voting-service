package com.system.controller.restaurant;

import com.system.controller.restaurant.RestaurantResourceAssembler.RestaurantResource;
import com.system.domain.Restaurant;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class RestaurantResourceAssembler extends ResourceAssemblerSupport<Restaurant, RestaurantResource> {

    public RestaurantResourceAssembler() {
        super(RestaurantsController.class, RestaurantResource.class);
    }

    @Override
    public RestaurantResource toResource(Restaurant restaurant) {
        RestaurantResource resource = createResourceWithId(restaurant.getId(), restaurant);
        resource.add(linkTo(RestaurantsController.class).slash(restaurant.getId()).slash("menus")
                         .withRel("restaurant-menus"));
        return resource;
    }

    @Override
    protected RestaurantResource instantiateResource(Restaurant entity) {
        return new RestaurantResource(entity);
    }

    public static class RestaurantResource extends Resource<Restaurant> {

        public RestaurantResource(Restaurant content) {
            super(content);
        }
    }

}
