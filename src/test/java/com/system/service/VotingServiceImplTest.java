package com.system.service;

import com.system.domain.Restaurant;
import com.system.domain.Vote;
import com.system.repository.RestaurantRepository;
import com.system.repository.VoteRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by mpereyma on 10/19/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class VotingServiceImplTest {

    private static final String MC_DONALDS = "McDonalds";
    private static final String KFC = "KFC";
    private static final String USER_1 = "user1";

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private VotingServiceImpl votingService;

    @Test(expected = IllegalArgumentException.class)
    public void testVoteRestaurantNotProvided() {
        Vote userVote = new Vote();
        votingService.voteForRestaurant(userVote);
    }

    @Test
    public void testVoteForRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant(1);
        restaurant.setName(MC_DONALDS);

        Vote userVote = new Vote();
        userVote.setUserName(USER_1);
        userVote.setRestaurant(restaurant);

        //begin of day
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        when(restaurantRepository.exists(userVote.getRestaurant().getId())).thenReturn(true);
        when(voteRepository.findFirstVoteByUserNameAndCreatedGreaterThan(USER_1, Timestamp.valueOf(today))).thenReturn(
            Optional.ofNullable(null));
        when(voteRepository.save(userVote)).thenReturn(userVote);

        Vote savedVote = votingService.voteForRestaurant(userVote);

        assertNotNull(savedVote);
        verify(voteRepository, atLeastOnce()).save(userVote);

    }

    @Test
    public void testVoteForTheSameRestaurant() throws Exception {
        Restaurant restaurant = new Restaurant(1);
        restaurant.setName(MC_DONALDS);

        Vote userVote = new Vote();
        userVote.setUserName(USER_1);
        userVote.setRestaurant(restaurant);

        //begin of day
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        when(restaurantRepository.exists(userVote.getRestaurant().getId())).thenReturn(true);
        when(voteRepository.findFirstVoteByUserNameAndCreatedGreaterThan(USER_1, Timestamp.valueOf(today))).thenReturn(
            Optional.ofNullable(userVote));

        Vote savedVote = votingService.voteForRestaurant(userVote);

        assertNotNull(savedVote);
        verify(voteRepository, never()).save(userVote);
    }

    @Test
    public void testVoteForDifferentRestaurant() throws Exception {
        Restaurant restaurantMD = new Restaurant(1);
        restaurantMD.setName(MC_DONALDS);

        Restaurant restaurantKFC = new Restaurant(2);
        restaurantKFC.setName(KFC);

        Vote userVote = new Vote();
        userVote.setUserName(USER_1);
        userVote.setRestaurant(restaurantMD);

        Vote existingVote = new Vote();
        existingVote.setUserName(USER_1);
        existingVote.setRestaurant(restaurantKFC);

        //begin of day
        LocalDateTime today = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        when(restaurantRepository.exists(userVote.getRestaurant().getId())).thenReturn(true);
        when(voteRepository.findFirstVoteByUserNameAndCreatedGreaterThan(USER_1, Timestamp.valueOf(today))).thenReturn(
            Optional.ofNullable(existingVote));
        when(voteRepository.save(existingVote)).thenReturn(existingVote);

        Vote savedVote = votingService.voteForRestaurant(userVote);

        assertNotNull(savedVote);
        assertTrue(savedVote.getRestaurant().equals(userVote.getRestaurant()));
        verify(voteRepository, atLeastOnce()).save(existingVote);

    }

}