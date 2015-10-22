package com.system.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.system.domain.Menu;

/**
 * Created by mpereyma on 10/15/15.
 */
@RepositoryRestResource(collectionResourceRel = "menu", path = "menu")
public interface MenuRepository extends CrudRepository<Menu, Long> {

}
