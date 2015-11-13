package com.system.controller.vote;

import com.system.controller.restaurant.RestaurantsController;
import com.system.domain.Vote;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class VoteResourceAssembler extends ResourceAssemblerSupport<Vote, VoteResourceAssembler.VoteResource> {

    public VoteResourceAssembler() {
        super(VotesController.class, VoteResource.class);
    }

    @Override
    public VoteResource toResource(Vote vote) {
        VoteResource resource = createResourceWithId(vote.getId(), vote);
        resource.add(linkTo(RestaurantsController.class).slash(vote.getRestaurant().getId())
                         .withRel("voted-restaurant"));
        return resource;
    }

    @Override
    protected VoteResource instantiateResource(Vote entity) {
        return new VoteResource(entity);
    }

    public static class VoteResource extends Resource<Vote> {

        public VoteResource(Vote content) {
            super(content);
        }
    }

}