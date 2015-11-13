package com.system.controller;

import com.google.common.collect.Sets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.system.Application;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class GettingStartedDocumentation {

    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .alwaysDo(document("{method-name}/{step}/",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint())))
            .build();
    }

    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/").accept(MediaTypes.HAL_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.votes", is(notNullValue())))
            .andExpect(jsonPath("_links.restaurants", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "user1")
    public void creatingARestaurant() throws JsonProcessingException, Exception {
        String restaurantLocation = createRestaurant("Tasty Food");
        MvcResult restaurant = getRestaurant(restaurantLocation);

        String menuLocation = createMenu(restaurantLocation);
        MvcResult menu = getMenu(menuLocation);

        restaurant = getRestaurant(getLink(menu, "menu-restaurant"));

        getMenus(getLink(restaurant, "restaurant-menus"));
        postingVote(restaurantLocation);
        String newRestaurantLocation = createRestaurant("Super Tasty Food");
        String voteLocation = postingVote(newRestaurantLocation);
        getVote(voteLocation);
    }

    private String postingVote(String restaurantLocation) throws Exception {
        Map<String, String> vote = new HashMap<String, String>();
        vote.put("restaurantUri", restaurantLocation);

        String voteLocation = this.mockMvc
            .perform(
                post("/votes").contentType(MediaTypes.HAL_JSON).content(
                    objectMapper.writeValueAsString(vote)))
            .andExpect(status().isAccepted())
            .andExpect(header().string("Location", notNullValue()))
            .andReturn().getResponse().getHeader("Location");
        return voteLocation;
    }

    MvcResult getVote(String voteLocation) throws Exception {
        return this.mockMvc.perform(get(voteLocation))
            .andExpect(status().isOk())
            .andExpect(jsonPath("userName", is(notNullValue())))
            .andExpect(jsonPath("_links.voted-restaurant", is(notNullValue())))
            .andReturn();
    }

    String createRestaurant(String name) throws Exception {
        Map<String, String> restaurant = new HashMap<String, String>();
        restaurant.put("name", name);

        String restaurantLocation = this.mockMvc
            .perform(
                post("/restaurants").contentType(MediaTypes.HAL_JSON).content(
                    objectMapper.writeValueAsString(restaurant)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", notNullValue()))
            .andReturn().getResponse().getHeader("Location");
        return restaurantLocation;
    }

    MvcResult getRestaurant(String restarantLocation) throws Exception {
        return this.mockMvc.perform(get(restarantLocation))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name", is(notNullValue())))
            .andExpect(jsonPath("_links.restaurant-menus", is(notNullValue())))
            .andReturn();
    }

    String createMenu(final String restaurantLocation) throws Exception, JsonProcessingException {
        Map<String, Object> menu = new HashMap<String, Object>();
        menu.put("restaurant", restaurantLocation);
        Map<String, String> dish1Map = new HashMap<String, String>();
        dish1Map.put("name", "dish1");
        dish1Map.put("price", Long.toString(10));
        Map<String, String> dish2Map = new HashMap<String, String>();
        dish2Map.put("name", "dish2");
        dish2Map.put("price", Long.toString(20));
        menu.put("dishes", Sets.newHashSet(dish1Map, dish2Map));

        String menuLocation = this.mockMvc
            .perform(
                post("/menus").contentType(MediaTypes.HAL_JSON).content(
                    objectMapper.writeValueAsString(menu)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", notNullValue()))
            .andReturn().getResponse().getHeader("Location");
        return menuLocation;
    }

    MvcResult getMenu(String menuLocation) throws Exception {
        return this.mockMvc.perform(get(menuLocation)).andExpect(status().isOk())
            .andExpect(jsonPath("dishes", is(notNullValue())))
            .andExpect(jsonPath("_links.menu-restaurant", is(notNullValue())))
            .andReturn();
    }

    void getMenus(String restaurantMenusLocation) throws Exception {
        this.mockMvc.perform(get(restaurantMenusLocation))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded.menus", hasSize(1)));
    }

    private String getLink(MvcResult result, String rel)
        throws UnsupportedEncodingException {
        return JsonPath.parse(result.getResponse().getContentAsString()).read(
            "_links." + rel + ".href");
    }
}