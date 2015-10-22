package com.system.controller;

import com.google.common.base.Preconditions;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

import com.system.domain.Vote;
import com.system.service.VotingService;

/**
 * Created by mpereyma on 10/18/15.
 */
@RestController
@RequestMapping("vote")
public class VoteController {

    @Value("${order.time.hour}")
    private int orderHour;

    @Value("${order.time.minute}")
    private int orderMinute;

    @Autowired
    private VotingService votingService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity voteForRestaurant(@RequestBody @NotEmpty Vote vote) {
        Preconditions.checkState(
            LocalTime.now().isBefore(LocalTime.of(orderHour, orderMinute)),
            String.format("Restaurant selection could not be changed after %1s:%2s", orderHour, orderMinute));

        //get user name from security context
        final String userName = getUserName();
        vote.setUserName(userName);

        votingService.voteForRestaurant(vote);

        //throw an exception or return just status
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private String getUserName() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
