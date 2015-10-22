package com.system.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.system.domain.Restaurant;

import java.util.Optional;

/**
 * Created by mpereyma on 10/15/15.
 */
@RepositoryRestResource(collectionResourceRel = "restaurant", path = "restaurant")
public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {

    Optional<Restaurant> findByName(final String name);

}