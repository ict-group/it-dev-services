package dev.it.services.model;

import dev.it.services.model.pojo.ActionValue;
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
import java.util.List;

@Entity
@Table(name = "performed_action_blog_post")

@FilterDef(name = "obj.blogpost_uuid", parameters = @ParamDef(name = "uuid", type = "string"))
@Filter(name = "obj.blogpost_uuid", condition = "uuid = :uuid")

@FilterDef(name = "from.last_update", parameters = @ParamDef(name = "last_update", type = "LocalDateTime"))
@Filter(name = "from.last_update", condition = "last_update >= :last_update")

@FilterDef(name = "to.last_update", parameters = @ParamDef(name = "last_update", type = "LocalDateTime"))
@Filter(name = "to.last_update", condition = "last_update <= :last_update")

@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public class PerformedActionBlogPost extends PanacheEntityBase {

    @Id
    public String uuid;

    public LocalDateTime last_update;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    public List<ActionValue> actions;

    public PerformedActionBlogPost() {

        actions = new ArrayList<>();
    }

}
