package com.system.controller.menu;

import com.system.controller.NestedContentResource;
import com.system.controller.ResourceDoesNotExistException;
import com.system.controller.menu.MenuResourceAssembler.MenuResource;
import com.system.controller.restaurant.RestaurantResourceAssembler;
import com.system.domain.Menu;
import com.system.domain.Restaurant;
import com.system.repository.MenuRepository;
import com.system.repository.RestaurantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("menus")
public class MenusController {

    private static final UriTemplate RESTAURANT_URI_TEMPLATE = new UriTemplate("/restaurants/{id}");

    private final MenuRepository repository;

    private final RestaurantResourceAssembler restaurantResourceAssembler;

    private final MenuResourceAssembler menuResourceAssembler;

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public MenusController(MenuRepository repository,
                           RestaurantRepository restaurantRepository,
                           RestaurantResourceAssembler restaurantResourceAssembler,
                           MenuResourceAssembler menuResourceAssembler) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantResourceAssembler = restaurantResourceAssembler;
        this.menuResourceAssembler = menuResourceAssembler;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    HttpHeaders create(@RequestBody MenuInput menuInput) {
        Menu menu = new Menu();
        menu.setRestaurant(getRestaurant(menuInput.getRestaurantUri()));
        menu.setDishes(menuInput.getDishes());

        this.repository.save(menu);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(MenusController.class).slash(menu.getId()).toUri());

        return httpHeaders;
    }

    @RequestMapping(method = RequestMethod.GET)
    NestedContentResource<MenuResource> all() {
        return new NestedContentResource<MenuResource>(
            this.menuResourceAssembler.toResources(this.repository.findAll()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Resource<Menu> menu(@PathVariable("id") long id) {
        Menu menu = findMenuById(id);
        return this.menuResourceAssembler.toResource(menu);
    }

    @RequestMapping(value = "/{id}/restaurant", method = RequestMethod.GET)
    Resource<Restaurant> menuRestaurant(@PathVariable("id") long id) {
        Menu menu = findMenuById(id);
        return this.restaurantResourceAssembler.toResource(menu.getRestaurant());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateMenu(@PathVariable("id") long id, @RequestBody MenuPatchInput menuPatchInput) {
        Menu menu = findMenuById(id);
        if (menuPatchInput.getRestaurantUri() != null) {
            menu.setRestaurant(getRestaurant(menuPatchInput.getRestaurantUri()));
        }
        if (menuPatchInput.getDishes() != null) {
            menu.setDishes(menuPatchInput.getDishes());
        }
        this.repository.save(menu);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable("id") long id) {
        this.repository.delete(id);
    }

    private Menu findMenuById(long id) {
        Menu menu = this.repository.findById(id);
        if (menu == null) {
            throw new ResourceDoesNotExistException();
        }
        return menu;
    }

    private Restaurant getRestaurant(URI restaurantLocation) {
        Restaurant restaurant = this.restaurantRepository.findById(extractTagId(restaurantLocation));
        if (restaurant == null) {
            throw new IllegalArgumentException("The restaurant '" + restaurantLocation
                                               + "' does not exist");
        }
        restaurant.setTodayMenu(repository.findTopByRestaurantOrderByCreatedDesc(restaurant));
        return restaurant;
    }

    private long extractTagId(URI restaurantLocation) {
        try {
            String idString = RESTAURANT_URI_TEMPLATE.match(restaurantLocation.toASCIIString()).get(
                "id");
            return Long.valueOf(idString);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("The restaurant '" + restaurantLocation + "' is invalid");
        }
    }
}