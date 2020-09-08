package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.api.util.TableKeyUtils;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Action;
import dev.it.services.model.BlogPost;
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

@Path(AppConstants.ACTIONS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class ActionServiceRs extends RsRepositoryServiceV3<Action, String> {

    @Inject
    S3Service s3Service;


    public ActionServiceRs() {
        super(Action.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " name asc";
    }


    @Override
    public PanacheQuery<Action> getSearch(String orderBy) throws Exception {

        PanacheQuery<Action> search;
        Sort sort = sort(orderBy);

        if (sort != null) {
            search = BlogPost.find("select a from Action a", sort);
        } else {
            search = BlogPost.find("select a from Action a");
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

    @Override
    protected void prePersist(Action action) throws Exception {

        action.uuid = TableKeyUtils.createSlug(action.name);
    }
}
