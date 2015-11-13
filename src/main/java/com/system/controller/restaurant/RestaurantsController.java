package com.system.controller.restaurant;

import com.system.controller.NestedContentResource;
import com.system.controller.ResourceDoesNotExistException;
import com.system.controller.menu.MenuResourceAssembler;
import com.system.controller.restaurant.RestaurantResourceAssembler.RestaurantResource;
import com.system.controller.menu.MenuResourceAssembler.MenuResource;
import com.system.domain.Restaurant;
import com.system.repository.MenuRepository;
import com.system.repository.RestaurantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


@RestController
@RequestMapping("restaurants")
public class RestaurantsController {

    private final RestaurantRepository repository;

    private final MenuRepository menuRepository;

    private final RestaurantResourceAssembler restaurantResourceAssembler;

    private final MenuResourceAssembler menuResourceAssembler;

    @Autowired
    public RestaurantsController(RestaurantRepository repository,
                                 MenuRepository menuRepository,
                                 RestaurantResourceAssembler restaurantResourceAssembler,
                                 MenuResourceAssembler menuResourceAssembler
    ) {
        this.repository = repository;
        this.menuRepository = menuRepository;
        this.restaurantResourceAssembler = restaurantResourceAssembler;
        this.menuResourceAssembler = menuResourceAssembler;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    HttpHeaders create(@RequestBody RestaurantInput restaurantInput) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantInput.getName());

        this.repository.save(restaurant);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(RestaurantsController.class).slash(restaurant.getId()).toUri());

        return httpHeaders;
    }

    @RequestMapping(method = RequestMethod.GET)
    NestedContentResource<RestaurantResource> all() {
        Iterable<Restaurant> restaurants = this.repository.findAll();
        for(Restaurant restaurant: restaurants){
            restaurant.setTodayMenu(menuRepository.findTopByRestaurantOrderByCreatedDesc(restaurant));
        }
        return new NestedContentResource<RestaurantResource>(
            this.restaurantResourceAssembler.toResources(restaurants));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Resource<Restaurant> restaurant(@PathVariable("id") final long id) {
        Restaurant restaurant = findRestaurantById(id);
        return this.restaurantResourceAssembler.toResource(restaurant);
    }

    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    ResourceSupport restaurantMenus(@PathVariable("id") long id) {
        return new NestedContentResource<MenuResource>(
            this.menuResourceAssembler.toResources(menuRepository.findByRestaurant(findRestaurantById(id))));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateRestaurant(@PathVariable("id") final long id, @RequestBody RestaurantPatchInput restaurantPatchInput) {
        Restaurant restaurant = findRestaurantById(id);
        if (restaurantPatchInput.getName() != null) {
            restaurant.setName(restaurantPatchInput.getName());
        }
        this.repository.save(restaurant);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable("id") long id) {
        this.repository.delete(id);
    }

    private Restaurant findRestaurantById(final long id) {
        Restaurant restaurant = this.repository.findById(id);
        if (restaurant == null) {
            throw new ResourceDoesNotExistException();
        }
        restaurant.setTodayMenu(menuRepository.findTopByRestaurantOrderByCreatedDesc(restaurant));
        return restaurant;
    }
}
