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
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
    @Override
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
            if (startRow == null) {
                startRow = 0;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            Integer listSize = getSearch(AppConstants.TABLE_NAME, orderBy, true, ui.getQueryParameters()).getFirstResult();
            Query search = getSearch(AppConstants.TABLE_NAME, orderBy, false, ui.getQueryParameters());
            List<Developer> list;
            if (listSize == 0) {
                list = new ArrayList<>();
            } else {
                list = search.setFirstResult(startRow).setMaxResults(pageSize).getResultList();
            }
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


    public Query getSearch(String tableName, String orderBy, boolean count, MultivaluedMap<String, String> multiMap) {
        StringBuffer sb = new StringBuffer();
        Map<String, Object> params = new HashMap<>();
        String separator = " where ";
        applyRestictions(sb, separator, params);
        String queryString;
        if (count) {
            queryString = createCountQuery(tableName, sb.toString(), 0);
        } else {
            queryString = createFindQuery(tableName, sb.toString(), 0);
        }
        Query query = getEntityManager().createNativeQuery(queryString);
        for (String param : params.keySet()) {
            query.setParameter(param, params.get(param));
        }
        return query;
    }


    public void applyRestictions(StringBuffer sb, String separator, Map<String, Object> parameters) {
        if (nn("json.hair_colour_obj")) {
            sb.append(separator).append("obj->> 'name' = 'hair_colour' and obj->>'value' = :hair_colour_obj ");
            parameters.put("hair_colour_obj", get("json.hair_colour_obj"));
            separator = " and ";
        }
        if (nn("json.number_of_degree_courses_gte")) {
            sb.append(separator).append("obj->> 'name' = 'number_of_degree_courses' and obj->>'value' >= :number_of_degree_courses_gte ");
            parameters.put("number_of_degree_courses_gte", get("json.number_of_degree_courses_gte"));
            separator = " and ";
        }
        if (nn("like.username")) {
            sb.append(separator).append("lower(username) LIKE :username");
            parameters.put("username", get("like.username"));
            separator = " and ";

        }
        if (nn("like.surname")) {
            //append and add paaremets
        }
        if (nn("like.tags")) {
            //append and add paaremets
        }
        if (nn("like.biography")) {
            //append and add paaremets
        }
        if (nn("from.birthdate")) {
            Date date = DateUtils.parseDate(get("from.birthdate"));
            //append and add paaremets
        }
        if (nn("to.birthdate")) {
            Date date = DateUtils.parseDate(get("to.birthdate"));
            //append and add paaremets
        }
    }


    private boolean uriInfoContainsJsonbParameters(UriInfo ui) {
        for (String key : ui.getQueryParameters().keySet())
            if (key.startsWith("json.")) {
                return true;
            }
        return false;
    }


    protected String createFindQuery(String tableName, String query, int paramCount) {
        String table = tableName != null ? tableName.trim() : getEntityClass().toString();
        if (query == null) {
            return "FROM " + table;
        }

        String trimmed = query.trim();
        if (trimmed.isEmpty()) {
            return "FROM " + table;
        }

        if (isNamedQuery(query)) {
            // we return named query as is
            return query;
        }

        String trimmedLc = trimmed.toLowerCase();
        if (trimmedLc.startsWith("from ") || trimmedLc.startsWith("select ")) {
            return query;
        }
        if (trimmedLc.startsWith("order by ")) {
            return "FROM " + table + " " + query;
        }
        if (trimmedLc.indexOf(' ') == -1 && trimmedLc.indexOf('=') == -1 && paramCount == 1) {
            query += " = ?1";
        }
        return "FROM " + table + " " + query;
    }

    protected boolean isNamedQuery(String query) {
        if (query == null || query.isEmpty()) {
            return false;
        }
        return query.charAt(0) == '#';
    }


    protected String createCountQuery(String tableName, String query, int paramCount) {

        String table = tableName != null ? tableName.trim() : getEntityClass().toString();
        if (query == null)
            return "SELECT COUNT(*) FROM " + table;

        String trimmed = query.trim();
        if (trimmed.isEmpty())
            return "SELECT COUNT(*) FROM " + table;

        String trimmedLc = trimmed.toLowerCase();
        if (trimmedLc.startsWith("from ")) {
            return "SELECT COUNT(*) " + query;
        }
        if (trimmedLc.startsWith("order by ")) {
            // ignore it
            return "SELECT COUNT(*) FROM " + table;
        }
        if (trimmedLc.indexOf(' ') == -1 && trimmedLc.indexOf('=') == -1 && paramCount == 1) {
            query += " = ?1";
        }
        return "SELECT COUNT(*) FROM " + table + " " + query;
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
