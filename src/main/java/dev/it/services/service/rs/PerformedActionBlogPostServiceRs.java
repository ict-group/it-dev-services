package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.PerformedActionBlogPost;
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
import java.time.LocalDateTime;

@Path(AppConstants.PERFORMED_ACTIONS_BLOGPOSTS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class PerformedActionBlogPostServiceRs extends RsRepositoryServiceV3<PerformedActionBlogPost, String> {

    public PerformedActionBlogPostServiceRs() {
        super(PerformedActionBlogPost.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " last_update desc";
    }


    @Override
    public PanacheQuery<PerformedActionBlogPost> getSearch(String orderBy) throws Exception {

        PanacheQuery<PerformedActionBlogPost> search;
        Sort sort = sort(orderBy);

        if (sort != null) {
            search = PerformedActionBlogPost.find("select a from PerformedActionBlogPost a", sort);
        } else {
            search = PerformedActionBlogPost.find("select a from PerformedActionBlogPost a");
        }
        if (nn("obj.blogpost_uuid")) {
            search
                    .filter("obj.blogpost_uuid", Parameters.with("blogpost_uuid", get("obj.blogpost_uuid")));
        }
        if (nn("obj.user_uuid")) {
            search
                    .filter("obj.user_uuid", Parameters.with("user_uuid", get("obj.user_uuid")));
        }

        if (nn("from.last_update")) {

            LocalDateTime date = LocalDateTime.parse(get("from.last_update"));

            search
                    .filter("from.last_update", Parameters.with("last_update", date));
        }

        if (nn("to.last_update")) {

            LocalDateTime date = LocalDateTime.parse(get("to.last_update"));

            search
                    .filter("to.last_update", Parameters.with("last_update", date));
        }

        return search;
    }
}
