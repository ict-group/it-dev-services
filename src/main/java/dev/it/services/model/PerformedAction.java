package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "performed_actions")

@FilterDef(name = "obj.blogpost_uuid", parameters = @ParamDef(name = "blogpost_uuid", type = "string"))
@Filter(name = "obj.blogpost_uuid", condition = "blogpost_uuid = :blogpost_uuid")

@FilterDef(name = "obj.user_uuid", parameters = @ParamDef(name = "user_uuid", type = "string"))
@Filter(name = "obj.user_uuid", condition = "user_uuid = :user_uuid")

public class PerformedAction extends PanacheEntityBase {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true, length = 100)
    @Id
    public String uuid;

    public String action;

    public String blogpost_uuid;

    public String user_uuid;

    public LocalDateTime creation_date;

    public LocalDateTime working_date;

    public PerformedAction() {
    }

}
