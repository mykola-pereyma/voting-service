package com.system.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.system.domain.Restaurant;

import java.util.Optional;

/**
 * Created by mpereyma on 10/15/15.
 */
@Repository( "restaurant")
public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {

    Restaurant findByName(final String name);

    Restaurant findById(final long id);

}