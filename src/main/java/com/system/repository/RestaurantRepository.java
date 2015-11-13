package com.system.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.system.domain.Restaurant;

/**
 * Created by mpereyma on 10/15/15.
 */
@Repository( "restaurant")
public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {

    Restaurant findByName(final String name);

    Restaurant findById(final long id);

}