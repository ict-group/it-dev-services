package dev.it.services.model;

import dev.it.services.model.pojo.PropertyValue;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.*;
import org.lorislab.quarkus.hibernate.types.json.JsonBinaryType;
import org.lorislab.quarkus.hibernate.types.json.JsonTypes;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)

@FilterDef(name = "obj.uuid", parameters = @ParamDef(name = "uuid", type = "string"))
@Filter(name = "obj.uuid", condition = "uuid = :uuid")

@FilterDef(name = "obj.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "obj.name", condition = "name = :name")

@FilterDef(name = "like.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "like.name", condition = "lower(name) LIKE :name")

@FilterDef(name = "like.description", parameters = @ParamDef(name = "description", type = "string"))
@Filter(name = "like.description", condition = "lower(description) LIKE :description")

@FilterDef(name = "like.tags", parameters = @ParamDef(name = "tags", type = "string"))
@Filter(name = "like.tags", condition = "lower(tags) LIKE :tags")

public class Project extends PanacheEntityBase {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true, length = 100)
    @Id
    public String uuid;
    public String name;
    public String description;
    public String tags;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    public List<PropertyValue> properties;

    public Project() {
        this.properties = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Project{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", properties=" + properties +
                '}';
    }
}
