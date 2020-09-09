package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "actions")

@FilterDef(name = "obj.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "obj.name", condition = "name = :name")

@FilterDef(name = "like.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "like.name", condition = "lower(name) LIKE :name")

public class Action extends PanacheEntityBase {

    @Id
    public String uuid;

    public String name;

    public String icon;

    public long operation_to_execute;

    public Action() {

        this.operation_to_execute = 1l;
    }

}
