package com.yanmastra.quarkusTraining.scheme;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class User extends PanacheEntityBase {
    @Id
    @Column(nullable = false, length = 36)
    public String id;

    @JsonProperty("username")
    @Column(length = 32, unique = true, nullable = false)
    public String username;

    @JsonProperty("password")
    public String password;

    @JsonProperty("name")
    public String name;

    @JsonProperty("address")
    public String address;

    @Override
    public void persist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        super.persist();
    }
}
