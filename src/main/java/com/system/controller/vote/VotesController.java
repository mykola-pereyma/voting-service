package com.system.controller.vote;

import com.google.common.base.Preconditions;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import com.system.controller.NestedContentResource;
import com.system.controller.ResourceDoesNotExistException;
import com.system.controller.menu.MenuInput;
import com.system.controller.menu.MenuResourceAssembler;
import com.system.controller.menu.MenusController;
import com.system.controller.restaurant.RestaurantResourceAssembler;
import com.system.domain.Menu;
import com.system.domain.Restaurant;
import com.system.domain.Vote;
import com.system.repository.MenuRepository;
import com.system.repository.RestaurantRepository;
import com.system.repository.VoteRepository;
import com.system.service.VotingService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by mpereyma on 10/18/15.
 */
@RestController
@RequestMapping("votes")
public class VotesController {

    private static final UriTemplate RESTAURANT_URI_TEMPLATE = new UriTemplate("/restaurants/{id}");

    private final VoteRepository voteRepository;

    private final RestaurantRepository restaurantRepository;

    private final VoteResourceAssembler voteResourceAssembler;

    @Value("${order.time.hour}")
    private int orderHour;

    @Value("${order.time.minute}")
    private int orderMinute;

    @Autowired
    private VotingService votingService;

    @Autowired
    public VotesController(VoteRepository voteRepository,
                           RestaurantRepository restaurantRepository,
                           VoteResourceAssembler voteResourceAssembler) {
        this.voteRepository = voteRepository;
        this.restaurantRepository = restaurantRepository;
        this.voteResourceAssembler = voteResourceAssembler;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(method = RequestMethod.POST)
    HttpHeaders create(@RequestBody VoteInput voteInput) {

        Preconditions.checkState(
            LocalTime.now().isBefore(LocalTime.of(orderHour, orderMinute)),
            String.format("Restaurant selection could not be changed after %1s:%2s", orderHour, orderMinute));


        //begin of day
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        //find previous vote and not update if restaurant remains unchanged
        Optional<Vote> voteOptional = voteRepository.findFirstVoteByUserNameAndCreatedGreaterThan(
            getUserName(), Timestamp.valueOf(today));

        final Vote vote;
        if(voteOptional.isPresent()){
            vote = voteOptional.get();
        } else {
            vote = new Vote();
            vote.setUserName(getUserName());
        }

        vote.setRestaurant(getRestaurant(voteInput.getRestaurantUri()));

        this.voteRepository.save(vote);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(VotesController.class).slash(vote.getId()).toUri());

        return httpHeaders;
    }

    @RequestMapping(method = RequestMethod.GET)
    NestedContentResource<VoteResourceAssembler.VoteResource> all() {
        return new NestedContentResource<VoteResourceAssembler.VoteResource>(
            this.voteResourceAssembler.toResources(this.voteRepository.findAll()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Resource<Vote> vote(@PathVariable("id") long id) {
        Vote vote = findVoteById(id);
        return this.voteResourceAssembler.toResource(vote);
    }

    private Vote findVoteById(long id) {
        Vote vote = this.voteRepository.findById(id);
        if (vote == null) {
            throw new ResourceDoesNotExistException();
        }
        return vote;
    }

    private String getUserName() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }


    private Restaurant getRestaurant(URI restaurantLocation) {
        Restaurant restaurant = this.restaurantRepository.findById(extractTagId(restaurantLocation));
        if (restaurant == null) {
            throw new IllegalArgumentException("The restaurant '" + restaurantLocation
                                               + "' does not exist");
        }
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