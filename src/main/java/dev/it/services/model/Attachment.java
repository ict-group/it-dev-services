package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "attachments")

@FilterDef(name = "obj.table_name", parameters = @ParamDef(name = "table_name", type = "string"))
@Filter(name = "obj.table_name", condition = "table_name = :table_name")

@FilterDef(name = "obj.table_key", parameters = @ParamDef(name = "table_key", type = "string"))
@Filter(name = "obj.table_key", condition = "table_key = :table_key")


public class Attachment extends PanacheEntityBase {

    @Id
    public String uuid;

    public String name;
    public String url;
    public String message_uuid;
    public String mimeType;
    public String size;

    public String table_name;
    public String table_key;

    public Attachment() {
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", message_uuid='" + message_uuid + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size='" + size + '\'' +
                ", table_name='" + table_name + '\'' +
                ", table_key='" + table_key + '\'' +
                '}';
    }
}
