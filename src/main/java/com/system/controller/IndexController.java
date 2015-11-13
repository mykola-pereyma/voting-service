package com.system.controller;

import com.system.controller.menu.MenusController;
import com.system.controller.restaurant.RestaurantsController;
import com.system.controller.vote.VotesController;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by mpereyma on 11/2/15.
 */
@RestController
@RequestMapping("/")
public class IndexController {

    @RequestMapping(method= RequestMethod.GET)
    public ResourceSupport index() {
        ResourceSupport index = new ResourceSupport();
        index.add(linkTo(VotesController.class).withRel("votes"));
        index.add(linkTo(RestaurantsController.class).withRel("restaurants"));
        index.add(linkTo(MenusController.class).withRel("menus"));
        return index;
    }

}