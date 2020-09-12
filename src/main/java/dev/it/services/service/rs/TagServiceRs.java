package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Tag;
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

@Path(AppConstants.TAGS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class TagServiceRs extends RsRepositoryServiceV3<Tag, String> {

    @Inject
    S3Service s3Service;


    public TagServiceRs() {
        super(Tag.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " name asc";
    }


    @Override
    public PanacheQuery<Tag> getSearch(String orderBy) throws Exception {

        PanacheQuery<Tag> search;
        Sort sort = sort(orderBy);

        if (sort != null) {
            search = Tag.find("select a from Tag a", sort);
        } else {
            search = Tag.find("select a from Tag a");
        }
        if (nn("obj.name")) {
            search
                    .filter("obj.name", Parameters.with("name", get("obj.name")));
        }
        if (nn("like.name")) {
            search
                    .filter("like.name", Parameters.with("name", likeParamToLowerCase("like.name")));
        }

        return search;
    }
}
