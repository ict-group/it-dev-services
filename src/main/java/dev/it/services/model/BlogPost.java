package dev.it.services.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogposts")

@FilterDef(name = "obj.type", parameters = @ParamDef(name = "type", type = "string"))
@Filter(name = "obj.type", condition = "type = :type")

@FilterDef(name = "like.title", parameters = @ParamDef(name = "title", type = "string"))
@Filter(name = "like.title", condition = "lower(title) LIKE :title")

@FilterDef(name = "like.tags", parameters = @ParamDef(name = "tags", type = "string"))
@Filter(name = "like.tags", condition = "lower(tags) LIKE :tags")

@FilterDef(name = "obj.author", parameters = @ParamDef(name = "author", type = "string"))
@Filter(name = "obj.author", condition = "author = :author")

@FilterDef(name = "obj.developer_uuid", parameters = @ParamDef(name = "developer_uuid", type = "string"))
@Filter(name = "obj.developer_uuid", condition = "developer_uuid = :developer_uuid")

public class BlogPost extends PanacheEntityBase {

    @Id
    public String uuid;
    // VIDEO - BLOG - NEWS
    public String type;
    public String title;
    public String content;
    public String content_preview;
    public String tags;
    public String author;
    public String video_url;
    public String developer_uuid;

    public LocalDateTime insert_date;
    public LocalDateTime update_date;

    public BlogPost() {
    }

    @Override
    public String toString() {
        return "BlogPost{" +
                "uuid='" + uuid + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", content_preview='" + content_preview + '\'' +
                ", tags='" + tags + '\'' +
                ", author='" + author + '\'' +
                ", developer_uuid='" + developer_uuid + '\'' +
                ", video_url='" + video_url + '\'' +
                ", insert_date=" + insert_date +
                ", update_date=" + update_date +
                '}';
    }
}
