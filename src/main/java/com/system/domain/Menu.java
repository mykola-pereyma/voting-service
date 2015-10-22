package com.system.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

/**
 * Created by mpereyma on 10/15/15.
 */
@Entity
public class Menu extends GenericEntity {

    @Size(min = 2, max = 5)
    @ManyToMany(cascade = CascadeType.ALL)
    Set<Dish> dishes;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Set<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(Set<Dish> dishes) {
        this.dishes = dishes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Menu menu = (Menu) o;

        if (id != menu.id) {
            return false;
        }
        if (restaurant != null ? !restaurant.equals(menu.restaurant) : menu.restaurant != null) {
            return false;
        }
        if (dishes != null ? !dishes.equals(menu.dishes) : menu.dishes != null) {
            return false;
        }
        return !(created != null ? !created.equals(menu.created) : menu.created != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (restaurant != null ? restaurant.hashCode() : 0);
        result = 31 * result + (dishes != null ? dishes.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }
}
