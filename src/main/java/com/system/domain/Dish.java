package com.system.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by mpereyma on 10/15/15.
 */
@Entity
public class Dish extends GenericEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dish dish = (Dish) o;

        if (id != dish.id) {
            return false;
        }
        if (name != null ? !name.equals(dish.name) : dish.name != null) {
            return false;
        }
        if (price != null ? !price.equals(dish.price) : dish.price != null) {
            return false;
        }
        return !(created != null ? !created.equals(dish.created) : dish.created != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }
}
