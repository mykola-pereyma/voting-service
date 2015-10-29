package com.system.controller;

import com.system.Application;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.HttpRetryException;
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
public class VoteControllerTest {

    public static final String MC_DONALDS = "McDonalds";
    public static final String USERNAME = "user1";
    public static final String PASSWORD = "upassword1";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Value("${order.time.hour}")
    private int orderHour;

    @Value("${order.time.minute}")
    private int orderMinute;

    private RestTemplate restTemplate = new TestRestTemplate(USERNAME, PASSWORD);

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

        Optional<Restaurant> restaurantOptional = restaurantRepository.findByName(MC_DONALDS);
        assertTrue(restaurantOptional.isPresent());
        Restaurant restaurant = restaurantOptional.get();
        restaurantRepository.delete(restaurant);
    }

    @Test
    public void testVoteForRestaurant() throws Exception {
        Vote userVote = new Vote();
        userVote.setRestaurant(restaurantRepository.findByName(MC_DONALDS).get());
        ResponseEntity response = restTemplate.postForEntity("http://localhost:8080/vote", userVote, Vote.class);
        assertNotNull(response);
        if(LocalTime.now().isBefore(LocalTime.of(orderHour, orderMinute))) {
            assertTrue(response.getStatusCode().equals(HttpStatus.NO_CONTENT));
        } else {
            assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
        }
    }

    @Test
    public void testVoteForRestaurantNotAuthentificated() throws Exception {
        Vote userVote = new Vote();
        userVote.setRestaurant(restaurantRepository.findByName(MC_DONALDS).get());
        RestTemplate restTemplateNoAuth = new TestRestTemplate("user1", "wrong");
        try {
            restTemplateNoAuth.postForEntity("http://localhost:8080/vote", userVote, Vote.class);
        } catch (ResourceAccessException e) {
            TestCase.assertEquals(((HttpRetryException) e.getCause()).responseCode(), HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Test
    public void testVoteWithNoRestaurant() throws Exception {
        Vote userVote = new Vote();
        ResponseEntity response = restTemplate.postForEntity("http://localhost:8080/vote", userVote, Vote.class);
        assertNotNull(response);
        assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testVoteForNonExistingRestaurant() throws Exception {
        Vote userVote = new Vote();
        userVote.setRestaurant(new Restaurant());
        ResponseEntity response = restTemplate.postForEntity("http://localhost:8080/vote", userVote, Vote.class);
        assertNotNull(response);
        assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }
}