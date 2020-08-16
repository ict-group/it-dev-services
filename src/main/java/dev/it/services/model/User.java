package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "users")

@FilterDef(name = "obj.username", parameters = @ParamDef(name = "username", type = "string"))
@Filter(name = "obj.username", condition = "username = :username")

@FilterDef(name = "like.username", parameters = @ParamDef(name = "username", type = "string"))
@Filter(name = "like.username", condition = "lower(username) LIKE :username")

@FilterDef(name = "like.roles", parameters = @ParamDef(name = "roles", type = "string"))
@Filter(name = "like.roles", condition = "lower(roles) LIKE :roles")

public class User extends PanacheEntityBase {

    @Id
    public String username;
    public String roles;

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", roles='" + roles + '\'' +
                '}';
    }
}
