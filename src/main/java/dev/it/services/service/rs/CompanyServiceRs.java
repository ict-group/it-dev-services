package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Company;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(AppConstants.COMPANIES_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class CompanyServiceRs extends RsRepositoryServiceV3<Company, String> {

    public CompanyServiceRs() {
        super(Company.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " name asc";
    }


    @Override
    public PanacheQuery<Company> getSearch(String orderBy) throws Exception {

        PanacheQuery<Company> search;
        Sort sort = sort(orderBy);

        if (sort != null) {
            search = Company.find("select a from Company a", sort);
        } else {
            search = Company.find("select a from Company a");
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
