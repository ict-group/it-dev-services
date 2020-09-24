package dev.it.services.service.rs;

import dev.it.api.service.v4.CreateService;
import dev.it.api.service.v4.ListService;
import dev.it.api.service.v4.RsServiceV4;
import dev.it.api.service.v4.RsServiceV4Search;
import dev.it.api.util.SlugUtils;
import dev.it.services.model.Action;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/api/v1/actions1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class TestService implements RsServiceV4<Action, String> {

    @Inject
    CreateService<Action, String> createService;

    @Inject
    ListService<Action, String> listService;

    //TODO: create a custom anotations in entity class to manage with Reflections.
    // @SearchParam(like = true, obj = true)
    // private String name;
    //
    // @searchParam(like = false, obj = true)
    // private String icon;
    //
    //
    RsServiceV4Search rsServiceV4Search = new RsServiceV4Search() {
        @Override
        public String getDefaultOrderBy() {
            return " name asc";
        }

        @Override
        public PanacheQuery getSearch(String orderBy) throws Exception {
            //sull classe bisogna aggiungere la annotation custom @OBJ_SEARCH, @LIKE_SEARCH
            PanacheQuery<Action> search;
            Sort sort = sort(orderBy);

            //this code can be dynamic with custom annotations & reflections api.
            if (sort != null) {
                search = Action.find("select a from Action a", sort);
            } else {
                search = Action.find("select a from Action a");
            }

            return search;
        }
    };

    @Transactional
    @Override
    public Response persistI(Action action) throws Exception {
        return createService.persist(action, SlugUtils.makeUniqueSlug(
                action.name,
                Action.class,
                createService.getEntityManager()), "uuid");
    }

    @Override
    public Response getListSizeI(UriInfo ui) {
        return listService.getListSize(ui, rsServiceV4Search);
    }

    @Override
    public Response getListI(Integer startRow, Integer pageSize, String orderBy, UriInfo ui) {
        return listService.getList(startRow, pageSize, orderBy, ui, rsServiceV4Search);
    }

}
