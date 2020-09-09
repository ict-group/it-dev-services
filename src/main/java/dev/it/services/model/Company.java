package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "companies")
public class Company extends PanacheEntityBase {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true, length = 100)
    @Id
    public String uuid;

    public String company;

    public long number_of;

    public Company() {

        this.number_of = 1l;
    }

    public Company(String company) {

        this.company = company;
        this.number_of = 1L;
    }
}
