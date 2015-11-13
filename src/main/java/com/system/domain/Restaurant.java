package com.system.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * Created by mpereyma on 10/15/15.
 */
@Entity
public class Restaurant extends GenericEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Transient
    private Menu todayMenu;

    public Restaurant(){}

    public Restaurant(final long id){
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Restaurant that = (Restaurant) o;

        if (id != that.id) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(created != null ? !created.equals(that.created) : that.created != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    public void setTodayMenu(Menu todayMenu) {
        this.todayMenu = todayMenu;
    }

    public Menu getTodayMenu() {
        return todayMenu;
    }
}
