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
import java.util.List;

@Entity
@Table(name = "performed_action_blog_post")

@FilterDef(name = "obj.blogpost_uuid", parameters = @ParamDef(name = "blogpost_uuid", type = "string"))
@Filter(name = "obj.blogpost_uuid", condition = "blogpost_uuid = :blogpost_uuid")

@FilterDef(name = "obj.user_uuid", parameters = @ParamDef(name = "user_uuid", type = "string"))
@Filter(name = "obj.user_uuid", condition = "user_uuid = :user_uuid")

@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public class PerformedActionBlogPost extends PanacheEntityBase {

    @Id
    public String uuid;

    public LocalDateTime last_update;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    public List<ActionValue> actions;

    public PerformedActionBlogPost() {
    }

}
