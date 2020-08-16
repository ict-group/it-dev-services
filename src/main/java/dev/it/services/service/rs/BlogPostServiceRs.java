package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.BlogPost;
import dev.it.services.model.Developer;
import dev.it.services.service.S3Service;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(AppConstants.BLOGPOSTS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class BlogPostServiceRs extends RsRepositoryServiceV3<BlogPost, String> {

    @Inject
    S3Service s3Service;

    public BlogPostServiceRs() {
        super(BlogPost.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " insert_date desc";
    }


    @Override
    public PanacheQuery<BlogPost> getSearch(String orderBy) throws Exception {
        PanacheQuery<BlogPost> search;
        Sort sort = sort(orderBy);
        if (sort != null) {
            search = BlogPost.find("select a from BlogPost a", sort);
        } else {
            search = BlogPost.find("select a from BlogPost a");
        }
        if (nn("obj.type")) {
            search
                    .filter("obj.type", Parameters.with("type", get("obj.type")));
        }
        if (nn("like.title")) {
            search
                    .filter("like.title", Parameters.with("title", likeParamToLowerCase("like.title")));
        }
        if (nn("like.tags")) {
            search
                    .filter("like.tags", Parameters.with("tags", likeParamToLowerCase("like.tags")));
        }
        if (nn("obj.author")) {
            search
                    .filter("obj.author", Parameters.with("author", get("obj.author")));
        }
        return search;
    }

}
