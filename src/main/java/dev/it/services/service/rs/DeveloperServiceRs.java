package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.api.util.DateUtils;
import dev.it.api.util.SlugUtils;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Developer;
import dev.it.services.model.pojo.CompanyEvent;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
