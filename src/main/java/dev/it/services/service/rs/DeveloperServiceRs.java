package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.api.util.DateUtils;
import dev.it.api.util.SlugUtils;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Developer;
import dev.it.services.model.pojo.CompanyEvent;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.*;

@Path(AppConstants.DEVELOPERS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class DeveloperServiceRs extends RsRepositoryServiceV3<Developer, String> {

    @Inject
    Event companyEvent;


    public DeveloperServiceRs() {
        super(Developer.class);
    }

    @Override
    protected String getDefaultOrderBy() {
        return " surname asc";
    }


    @GET
    @Transactional
    public Response getList(
            @DefaultValue("0") @QueryParam("startRow") Integer startRow,
            @DefaultValue("10") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy, @Context UriInfo ui) {
        if (!uriInfoContainsJsonbParameters(ui)) {
            //JPA WAY
            return super.getList(startRow, pageSize, orderBy, ui);
        } else {
            //NATIVE WAY
            return nativeList(startRow, pageSize, orderBy, ui);
        }

    }


    public Response nativeList(Integer startRow,
                               Integer pageSize,
                               String orderBy,
                               UriInfo ui) {
        try {
            long listSize = 0;
            List<Developer> list = new ArrayList<>();

            return Response
                    .status(Response.Status.OK)
                    .entity(list)
                    .header("Access-Control-Expose-Headers", "startRow, pageSize, listSize")
                    .header("startRow", startRow)
                    .header("pageSize", pageSize)
                    .header("listSize", listSize)
                    .build();
        } catch (Exception e) {
            logger.errorv(e, "getList");
            return jsonErrorMessageResponse(e);
        }
    }


    private boolean uriInfoContainsJsonbParameters(UriInfo ui) {
        for (String key : ui.getQueryParameters().keySet())
            if (key.startsWith("json.")) {
                return true;
            }
        return false;
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

    @Override
    protected void prePersist(Developer developer) throws Exception {
        String generatedUuid = SlugUtils.makeUniqueSlug(developer.surname + "-" + developer.name, Developer.class, getEntityManager());
        developer.uuid = generatedUuid;
        developer.insert_date = LocalDateTime.now();
    }

    @Override
    protected void postPersist(Developer developer) throws Exception {
        if (developer != null) {
            if (developer.companies != null) {
                String[] companies = developer.companies.split(",");
                Arrays.stream(companies).forEach(companyName -> companyEvent.fireAsync(new CompanyEvent(companyName.toLowerCase().trim(), true)));
            }
        }
    }

    @Override
    protected Developer preUpdate(Developer developer) throws Exception {
        String oldCompanies;
        Developer existingDeveloper = Developer.findById(developer.uuid);
        oldCompanies = existingDeveloper.companies;
        if (!oldCompanies.equals(developer.companies)) {
            workCompanies(developer, oldCompanies);
        }
        developer.update_date = LocalDateTime.now();
        return developer;
    }

    protected void workCompanies(Developer developer, String oldCompanies) throws Exception {
        String[] existingCompanies = oldCompanies.split(",");
        String[] newCompanies = developer.companies.split(",");

        //Find the similar elements that will not be touched
        Set<String> similar = new HashSet(Arrays.asList(existingCompanies));
        similar.retainAll(Arrays.asList(newCompanies));

        //Remove all tags removed from blogspot
        Set<String> toRemoveCompanies = new HashSet<>(Arrays.asList(existingCompanies));
        toRemoveCompanies.removeAll(similar);
        toRemoveCompanies.stream().forEach(tagName -> companyEvent.fireAsync(new CompanyEvent(tagName.toLowerCase().trim(), false)));

        //Add all tags added to blogspot
        Set<String> newCompaniesSet = new HashSet<>(Arrays.asList(newCompanies));
        newCompaniesSet.removeAll(similar);
        newCompaniesSet.stream().forEach(tagName -> companyEvent.fireAsync(new CompanyEvent(tagName.toLowerCase().trim(), true)));
    }
}
