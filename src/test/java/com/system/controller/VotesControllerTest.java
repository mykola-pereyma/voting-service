package com.system.controller;

import com.system.Application;
import com.system.ScheduleConfigurer;
import com.system.controller.vote.VoteInput;
import com.system.domain.Restaurant;
import com.system.domain.Vote;
import com.system.repository.RestaurantRepository;
import com.system.repository.VoteRepository;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.HttpRetryException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by mpereyma on 10/20/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
public class VotesControllerTest {

    public static final String MC_DONALDS = "McDonalds";
    public static final String USERNAME = "user1";
    public static final String PASSWORD = "upassword1";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    ScheduleConfigurer scheduleConfigurer;

    private final RestTemplate restTemplate = new TestRestTemplate(USERNAME, PASSWORD);

    @Before
    public void setUp() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(MC_DONALDS);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        assertNotNull(savedRestaurant);
    }

    @After
    public void tearDown() {
        Optional<Vote> vote = voteRepository.findFirstVoteByUserNameAndCreatedGreaterThan(
            USERNAME, Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0))));
        if(vote.isPresent()){
            voteRepository.delete(vote.get());
        }

        Restaurant restaurant = restaurantRepository.findByName(MC_DONALDS);
        assertTrue(restaurant != null);
        restaurantRepository.delete(restaurant);
    }

    @Test
    public void testVoteForRestaurant() throws Exception {
        Restaurant restaurant = restaurantRepository.findByName(MC_DONALDS);
        VoteInput voteInput = new VoteInput(new URI("http://localhost:8080/restaurants/" + restaurant.getId()));
        ResponseEntity response = restTemplate.postForEntity("http://localhost:8080/votes", voteInput, Vote.class);
        assertNotNull(response);
        if(LocalTime.now().isBefore(LocalTime.of(scheduleConfigurer.getOrderHour(), scheduleConfigurer.getOrderMinute()))) {
            assertTrue(response.getStatusCode().equals(HttpStatus.ACCEPTED));
        } else {
            assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void testVoteForRestaurantNotAuthentificated() throws Exception {
        Restaurant restaurant = restaurantRepository.findByName(MC_DONALDS);
        VoteInput voteInput = new VoteInput(new URI("http://localhost:8080/restaurants/" + restaurant.getId()));
        RestTemplate restTemplateNoAuth = new TestRestTemplate("user1", "wrong");
        try {
            restTemplateNoAuth.postForEntity("http://localhost:8080/votes", voteInput, Vote.class);
        } catch (ResourceAccessException e) {
            TestCase.assertEquals(((HttpRetryException) e.getCause()).responseCode(), HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Test
    public void testVoteWithNoRestaurant() throws Exception {
        VoteInput voteInput = new VoteInput(null);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:8080/votes", voteInput, Vote.class);
        assertNotNull(response);
        assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testVoteForNonExistingRestaurant() throws Exception {
        Vote userVote = new Vote();
        userVote.setRestaurant(new Restaurant());
        ResponseEntity response = restTemplate.postForEntity("http://localhost:8080/votes", userVote, Vote.class);
        assertNotNull(response);
        assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }
}