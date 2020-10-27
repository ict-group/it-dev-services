package dev.it.api.service.v4;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.it.api.service.RsResponseService.jsonErrorMessageResponse;

@ApplicationScoped
public class ListService<T extends PanacheEntityBase, U> {
    private static final long serialVersionUID = 1L;
    protected Logger logger = Logger.getLogger(getClass());

    @Inject
    EntityManager entityManager;

    @Context
    UriInfo ui;

    public Response getListSize(UriInfo ui, RsServiceV4Search rsServiceV4Search) {
        logger.info("getListSize");
        Map<String, Object> params = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder();
        try {
            PanacheQuery<T> search = rsServiceV4Search.getSearch(null);
            long listSize = search.count();
            return Response.status(Response.Status.OK).entity(listSize)
                    .header("Access-Control-Expose-Headers", "listSize")
                    .header("listSize", listSize).build();
        } catch (Exception e) {
            logger.errorv(e, "getListSize");
            return jsonErrorMessageResponse(e);
        }
    }

    public Response getList(Integer startRow, Integer pageSize, String orderBy, UriInfo ui, RsServiceV4Search<T> rsServiceV4Search) {
        logger.info("getList");
        try {
            PanacheQuery<T> search = rsServiceV4Search.getSearch(orderBy);
            long listSize = search.count();
            List<T> list;
            if (listSize == 0) {
                list = new ArrayList<>();
            } else {
                int currentPage = 0;
                if (pageSize != 0) {
                    currentPage = startRow / pageSize;
                } else {
                    pageSize = Long.valueOf(listSize).intValue();
                }
                list = search.page(Page.of(currentPage, pageSize)).list();
            }
            //postList(list);

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

    public boolean nn(String key) {
        return ui.getQueryParameters().containsKey(key)
                && ui.getQueryParameters().getFirst(key) != null
                && !ui.getQueryParameters().getFirst(key).trim().isEmpty();
    }
}
