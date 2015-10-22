package com.system.service;

import com.google.common.base.Preconditions;

import com.system.domain.Restaurant;
import com.system.domain.Vote;
import com.system.repository.RestaurantRepository;
import com.system.repository.VoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Created by mpereyma on 10/18/15.
 */
@Service
public class VotingServiceImpl implements VotingService {

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Override
    @Transactional
    public Vote voteForRestaurant(Vote userVote) {
        Restaurant restaurant = userVote.getRestaurant();
        Preconditions.checkArgument(restaurant != null, "Restaurant is not defined in vote");
        Preconditions.checkArgument(restaurantRepository.exists(restaurant.getId()), "Restaurant does not exist");

        //begin of day
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        //find previous vote and not update if restaurant remains unchanged
        Optional<Vote> voteOptional = voteRepository.findFirstVoteByUserNameAndCreatedGreaterThan(
            userVote.getUserName(), Timestamp.valueOf(today));
        if (voteOptional.isPresent()) {
            Vote existingVote = voteOptional.get();
            if (existingVote.getRestaurant().getId() != restaurant.getId()) {
                existingVote.setRestaurant(restaurant);
                return voteRepository.save(existingVote);
            } else {
                return existingVote;
            }
        } else {
            return voteRepository.save(userVote);
        }
    }
}
