package com.system.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by mpereyma on 10/19/15.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @CreatedDate
    protected Date created;

    public GenericEntity(){

    }

    public GenericEntity(final long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
