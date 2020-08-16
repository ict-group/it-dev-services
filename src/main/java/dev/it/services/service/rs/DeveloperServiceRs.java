package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.api.util.DateUtils;
import dev.it.services.management.AppConstants;
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
import java.util.Date;

@Path(AppConstants.DEVELOPERS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class DeveloperServiceRs extends RsRepositoryServiceV3<Developer, String> {

    @Inject
    S3Service s3Service;

    public DeveloperServiceRs() {
        super(Developer.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " surname asc";
    }


    @Override
    public PanacheQuery<Developer> getSearch(String orderBy) throws Exception {
        PanacheQuery<Developer> search;
        Sort sort = sort(orderBy);
        if (sort != null) {
            search = Developer.find("select a from Developer a", sort);
        } else {
            search = Developer.find("select a from Developer a");
        }
        if (nn("like.username")) {
            search
                    .filter("like.username", Parameters.with("username", likeParamToLowerCase("like.username")));
        }
        if (nn("like.surname")) {
            search
                    .filter("like.surname", Parameters.with("surname", likeParamToLowerCase("like.surname")));
        }
        if (nn("like.tags")) {
            search
                    .filter("like.tags", Parameters.with("tags", likeParamToLowerCase("like.tags")));
        }
        if (nn("like.biography")) {
            search
                    .filter("like.biography", Parameters.with("biography", likeParamToLowerCase("like.biography")));
        }
        if (nn("from.birthdate")) {
            Date date = DateUtils.parseDate(get("from.birthdate"));
            search
                    .filter("from.birthdate", Parameters.with("birthdate", DateUtils.toBeginOfDay(date)));
        }
        if (nn("to.birthdate")) {
            Date date = DateUtils.parseDate(get("to.birthdate"));
            search
                    .filter("to.birthdate", Parameters.with("birthdate", DateUtils.toEndOfDay(date)));
        }
        return search;
    }

}
