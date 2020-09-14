package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "companies")

@FilterDef(name = "obj.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "obj.name", condition = "name = :name")

@FilterDef(name = "like.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "like.name", condition = "lower(name) LIKE :name")

public class Company extends PanacheEntityBase {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true, length = 100)
    @Id
    public String uuid;
    public String name;
    public long number_of;

    public Company() {
        this.number_of = 1l;
    }

    public Company(String name) {
        this.name = name;
        this.number_of = 1L;
    }
}
