package com.system.repository;


import com.system.domain.Vote;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

/**
 * Created by mpereyma on 10/15/15.
 */
@Repository
public interface VoteRepository extends CrudRepository<Vote, Long> {

    Optional<Vote> findFirstVoteByUserNameAndCreatedGreaterThan(
        final String userName, final Date created);

    Vote findById(long id);
}