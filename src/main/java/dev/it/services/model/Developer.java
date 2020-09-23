package dev.it.services.model;

import dev.it.services.model.pojo.PropertyValue;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.*;
import org.lorislab.quarkus.hibernate.types.json.JsonBinaryType;
import org.lorislab.quarkus.hibernate.types.json.JsonTypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static dev.it.services.model.Developer.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME)
@FilterDef(name = "obj.uuid", parameters = @ParamDef(name = "uuid", type = "string"))
@Filter(name = "obj.uuid", condition = "uuid = :uuid")

@FilterDef(name = "like.username", parameters = @ParamDef(name = "username", type = "string"))
@Filter(name = "like.username", condition = "lower(username) LIKE :username")

@FilterDef(name = "like.surname", parameters = @ParamDef(name = "surname", type = "string"))
@Filter(name = "like.surname", condition = "lower(surname) LIKE :surname")

@FilterDef(name = "like.tags", parameters = @ParamDef(name = "tags", type = "string"))
@Filter(name = "like.tags", condition = "lower(tags) LIKE :tags")

@FilterDef(name = "like.biography", parameters = @ParamDef(name = "biography", type = "string"))
@Filter(name = "like.biography", condition = "lower(biography) LIKE :biography")

@FilterDef(name = "from.birthdate", parameters = @ParamDef(name = "birthdate", type = "string"))
@Filter(name = "from.birthdate", condition = "birthdate >= :birthdate")

@FilterDef(name = "to.birthdate", parameters = @ParamDef(name = "birthdate", type = "string"))
@Filter(name = "to.birthdate", condition = "birthdate <= :birthdate")

@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public class Developer extends PanacheEntityBase {

    public static final String TABLE_NAME = "developers";

    @Id
    public String uuid;
    public String username;
    public String name;
    public String surname;
    public String tags;
    public String biography;
    public String biography_preview;
    public String lastCompany;
    public String photo_url;
    public Date birthdate;
    public String companies;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    public List<PropertyValue> properties;

    public LocalDateTime insert_date;
    public LocalDateTime update_date;

    public Developer() {
        this.properties = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Developer{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", tags='" + tags + '\'' +
                ", biography='" + biography + '\'' +
                ", biography_preview='" + biography_preview + '\'' +
                ", lastCompany='" + lastCompany + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", birthdate=" + birthdate +
                ", companies='" + companies + '\'' +
                ", properties=" + properties +
                ", insert_date=" + insert_date +
                ", update_date=" + update_date +
                '}';
    }
}
