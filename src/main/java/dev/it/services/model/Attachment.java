package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.util.Date;


@Entity
@Table(name = "attachments")

@FilterDef(name = "obj.uuid", parameters = @ParamDef(name = "uuid", type = "string"))
@Filter(name = "obj.uuid", condition = "uuid = :uuid")

@FilterDef(name = "like.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "like.name", condition = "name LIKE :name")

@FilterDef(name = "from.creation_date", parameters = @ParamDef(name = "creation_date", type = "string"))
@Filter(name = "from.creation_date", condition = "creation_date >= :creation_date")

@FilterDef(name = "to.creation_date", parameters = @ParamDef(name = "creation_date", type = "string"))
@Filter(name = "to.creation_date", condition = "creation_date <= :creation_date")

public class Attachment extends PanacheEntityBase {

    @Id
    public String uuid;

    public String name;
    public String s3name;

    public Date creation_date;
    public String mime_type;
    public String s3_url;

    public String external_uuid;
    public String external_type;

    public Attachment() {
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", s3name='" + s3name + '\'' +
                ", creation_date=" + creation_date +
                ", mime_type='" + mime_type + '\'' +
                ", external_type='" + external_type + '\'' +
                ", external_uuid='" + external_uuid + '\'' +
                ", s3_url='" + s3_url + '\'' +
                '}';
    }
}
