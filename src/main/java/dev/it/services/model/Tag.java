package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "tags")
public class Tag extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true, length = 100)
    public String uuid;

    public String name;

    public long numberOf;

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
        this.numberOf = 1L;
    }
}
