package com.system.controller.menu;

import com.system.domain.Menu;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class MenuResourceAssembler extends ResourceAssemblerSupport<Menu, MenuResourceAssembler.MenuResource> {

    public MenuResourceAssembler() {
        super(MenusController.class, MenuResource.class);
    }

    @Override
    public MenuResource toResource(Menu menu) {
        MenuResource resource = createResourceWithId(menu.getId(), menu);
        resource.add(linkTo(MenusController.class).slash(menu.getId()).slash("restaurant")
                         .withRel("menu-restaurant"));
        return resource;
    }

    @Override
    protected MenuResource instantiateResource(Menu entity) {
        return new MenuResource(entity);
    }

    public static class MenuResource extends Resource<Menu> {

        public MenuResource(Menu content) {
            super(content);
        }
    }

}