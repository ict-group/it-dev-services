package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "developers")
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

//@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public class Developer extends PanacheEntityBase {

    @Id
    public String uuid;

    public String username;
    public String name;
    public String surname;
    public String tags;
    public String biography;
    public String lastCompany;
    public String photo_url;
    public Date birthdate;

//    @Type(type = "jsonb")
//    @Column(columnDefinition = "jsonb")
//    public List<PropertyValue> properties;

    public Date creation_date;

    public Developer() {
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
                ", lastCompany='" + lastCompany + '\'' +
                ", photo_url='" + photo_url + '\'' +
                ", birthdate=" + birthdate +
                ", creation_date=" + creation_date +
                '}';
    }
}
