package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Developer;
import dev.it.services.model.Project;
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

@Path(AppConstants.PROJECTS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class ProjectServiceRs extends RsRepositoryServiceV3<Project, String> {
    
    public ProjectServiceRs() {
        super(Project.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " name asc";
    }


    @Override
    public PanacheQuery<Project> getSearch(String orderBy) throws Exception {
        PanacheQuery<Project> search;
        Sort sort = sort(orderBy);
        if (sort != null) {
            search = Project.find("select a from Project a", sort);
        } else {
            search = Project.find("select a from Project a");
        }
        if (nn("obj.uuid")) {
            search
                    .filter("obj.uuid", Parameters.with("uuid", get("obj.uuid")));
        }
        if (nn("obj.name")) {
            search
                    .filter("obj.name", Parameters.with("name", get("obj.name")));
        }
        if (nn("like.name")) {
            search
                    .filter("like.name", Parameters.with("name", likeParamToLowerCase("like.name")));
        }
        if (nn("like.tags")) {
            search
                    .filter("like.tags", Parameters.with("tags", likeParamToLowerCase("like.tags")));
        }
        return search;
    }

}
