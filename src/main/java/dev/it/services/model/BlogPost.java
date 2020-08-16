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

public class BlogPost extends PanacheEntityBase {

    @Id
    public String uuid;
    // VIDEO - BLOG - NEWS
    public String type;
    public String title;
    public String content;
    public String tags;
    public String author;

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
                ", tags='" + tags + '\'' +
                ", author='" + author + '\'' +
                ", insert_date=" + insert_date +
                ", update_date=" + update_date +
                '}';
    }
}
