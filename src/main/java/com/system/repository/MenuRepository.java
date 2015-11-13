package com.system.repository;

import com.system.domain.Menu;
import com.system.domain.Restaurant;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Created by mpereyma on 10/15/15.
 */
@Repository("menu")
public interface MenuRepository extends CrudRepository<Menu, Long> {

    Menu findById(long id);

    Collection<Menu> findByRestaurant(Restaurant Restaurant);

    Menu findTopByRestaurantOrderByCreatedDesc(Restaurant Restaurant);


}
