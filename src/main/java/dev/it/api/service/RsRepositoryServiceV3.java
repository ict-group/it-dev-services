package dev.it.api.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RsRepositoryServiceV3<T extends PanacheEntityBase, U> extends RsResponseService implements
        Serializable {

    private static final long serialVersionUID = 1L;

    protected Logger logger = Logger.getLogger(getClass());

    private Class<T> entityClass;

    @Inject
    EntityManager entityManager;

    @Inject
    SecurityIdentity securityIdentity;

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected SecurityIdentity getCurrentUser() {
        return securityIdentity;
    }

    public RsRepositoryServiceV3(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public RsRepositoryServiceV3() {
    }

    protected void prePersist(T object) throws Exception {
    }


    @POST
    @Transactional
    public Response persist(T object) {
        logger.info("persist");
        try {
            prePersist(object);
        } catch (Exception e) {
            logger.errorv(e, "persist");
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }

        try {
            entityManager.persist(object);
            if (object == null) {
                logger.error("Failed to create resource: " + object);
                return jsonErrorMessageResponse(object);
            } else {
                return Response.status(Status.OK).entity(object).build();
            }
        } catch (Exception e) {
            logger.errorv(e, "persist");
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postPersist(object);
            } catch (Exception e) {
                logger.errorv(e, "persist");
            }
        }
    }

    protected void postPersist(T object) throws Exception {
    }

    protected void postFetch(T object) throws Exception {
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response fetch(@PathParam("id") U id) {
        logger.info("fetch: " + id);

        try {
            T t = find(id);
            if (t == null) {
                return handleObjectNotFoundRequest(id);
            } else {
                try {
                    postFetch(t);
                } catch (Exception e) {
                    logger.errorv(e, "fetch: " + id);
                }
                return Response.status(Status.OK).entity(t).build();
            }
        } catch (NoResultException e) {
            logger.errorv(e, "fetch: " + id);
            return jsonMessageResponse(Status.NOT_FOUND, id);
        } catch (Exception e) {
            logger.errorv(e, "fetch: " + id);
            return jsonErrorMessageResponse(e);
        }
    }

    protected T preUpdate(T object) throws Exception {
        return object;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") U id, T object) {
        logger.info("update:" + id);

        try {
            object = preUpdate(object);
        } catch (Exception e) {
            logger.errorv(e, "update:" + id);
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        try {
            entityManager.merge(object);
            return Response.status(Status.OK).entity(object).build();
        } catch (Exception e) {
            logger.errorv(e, "update:" + id);
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postUpdate(object);
            } catch (Exception e) {
                logger.errorv(e, "update:" + id);
            }
        }
    }

    /**
     * concepita per chiamare robe async dopo l'update (o cmq robe fuori dalla tx principale che non rollbacka se erorri qui)
     *
     * @param object
     * @throws Exception
     */
    protected void postUpdate(T object) throws Exception {
    }

    protected void preDelete(U id) throws Exception {
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") U id) {
        logger.info("delete: " + id);

        try {
            preDelete(id);
        } catch (Exception e) {
            logger.errorv(e, "delete: " + id);
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        T t;
        try {
            t = find(id);
            if (t == null) {
                return handleObjectNotFoundRequest(id);
            }
        } catch (Exception e) {
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        try {
            toDelete(t);
            postDelete(id);
            return jsonMessageResponse(Status.NO_CONTENT, id);
        } catch (NoResultException e) {
            logger.errorv(e, "delete: " + id);
            return jsonMessageResponse(Status.NOT_FOUND, id);
        } catch (Exception e) {
            logger.errorv(e, "delete: " + id);
            return jsonErrorMessageResponse(e);
        }
    }

    protected void postDelete(U id) throws Exception {
    }

    @GET
    @Path("/{id}/exist")
    public Response exist(@PathParam("id") U id) {
        logger.info("exist: " + id);

        try {
            boolean exist = find(id) != null;
            if (!exist) {
                return handleObjectNotFoundRequest(id);
            } else {
                return jsonMessageResponse(Status.OK, id);
            }
        } catch (Exception e) {
            logger.errorv(e, "exist: " + id);
            return jsonErrorMessageResponse(e);
        }
    }

    @GET
    @Path("/listSize")
    @Transactional
    public Response getListSize(@Context UriInfo ui) {
        logger.info("getListSize");
        Map<String, Object> params = new HashMap<>();
        StringBuilder queryBuilder = new StringBuilder();
        try {
            PanacheQuery<T> search = getSearch(null);
            long listSize = search.count();
            return Response.status(Status.OK).entity(listSize)
                    .header("Access-Control-Expose-Headers", "listSize")
                    .header("listSize", listSize).build();
        } catch (Exception e) {
            logger.errorv(e, "getListSize");
            return jsonErrorMessageResponse(e);
        }
    }

    @GET
    @Transactional
    public Response getList(
            @DefaultValue("0") @QueryParam("startRow") Integer startRow,
            @DefaultValue("10") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy, @Context UriInfo ui) {

        logger.info("getList");
        try {
            PanacheQuery<T> search = getSearch(orderBy);
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
            postList(list);

            return Response
                    .status(Status.OK)
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

    protected void postList(List<T> list) throws Exception {
    }

    protected void preUpdateProp(U id, String name, String value, String new_value) throws Exception {
    }

    @PUT
    @Transactional
    @Path("{uuid}/updateprop/{name}/value/{value}/with/{new_value}")
    public Response updateProperty(@PathParam("uuid") U id,
                                   @PathParam("name") String name,
                                   @PathParam("value") String value,
                                   @PathParam("new_value") String new_value) {

        logger.info("update property at: " + id);

        try {
            preUpdateProp(id, name, value, new_value);
        } catch (Exception e) {
            logger.errorv(e, "update property at: " + id);
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        T object;
        try {
            object = find(id);
            if (object == null) {
                return handleObjectNotFoundRequest(id);
            }
        } catch (Exception e) {
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        try {
            updateProp(object, name, value, new_value);
            entityManager.merge(object);
            return Response.status(Status.OK).entity(object).build();
        } catch (Exception e) {
            logger.errorv(e, "update property at: " + id);
            return jsonErrorMessageResponse(e);
        }
        finally {
            try {
                postUpdateProp(id, name, value, new_value);
            } catch (Exception e) {
                logger.errorv(e, "update property at:" + id);
            }
        }
    }

    protected T updateProp(T t, String name, String value, String new_value) {

        return t;
    }

    protected void postUpdateProp(U id, String name, String value, String new_value) throws Exception {
    }


    protected void preDeleteProp(U id, String name, String value) throws Exception {
    }

    @DELETE
    @Transactional
    @Path("{uuid}/deleteprop/{name}/value/{value}")
    public Response deleteProperty(@PathParam("uuid") U id,
                                   @PathParam("name") String name,
                                   @PathParam("value") String value) {

        logger.info("delete property at: " + id);

        try {
            preDeleteProp(id, name, value);
        } catch (Exception e) {
            logger.errorv(e, "delete property at: " + id);
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        T t;
        try {
            t = find(id);
            if (t == null) {
                return handleObjectNotFoundRequest(id);
            }
        } catch (Exception e) {
            return jsonMessageResponse(Status.BAD_REQUEST, e);
        }
        try {
            removeProp(t, name, value);
            entityManager.merge(t);
            postDeleteProp(id, name, value);
            return jsonMessageResponse(Status.NO_CONTENT, id);
        } catch (NoResultException e) {
            logger.errorv(e, "delete property at: " + id);
            return jsonMessageResponse(Status.NOT_FOUND, id);
        } catch (Exception e) {
            logger.errorv(e, "delete property at: " + id);
            return jsonErrorMessageResponse(e);
        }
    }

    protected void removeProp(T t, String name, String value) {
    }

    protected void postDeleteProp(U id, String name, String value) throws Exception {
    }

    /**
     * Gestisce la risposta a seguito di un oggetto non trovato
     *
     * @param id
     * @return
     */
    protected Response handleObjectNotFoundRequest(U id) {
        String errorMessage = MessageFormat.format("Object [{0}] with id [{1}] not found",
                entityClass.getCanonicalName(), id);
        return jsonMessageResponse(Status.NOT_FOUND, errorMessage);
    }

    protected Response handleObjectNotFoundRequest(U id, String name) {
        String errorMessage = MessageFormat.format("Object [{0}] with id [{1}] not found",
                name, id);
        return jsonMessageResponse(Status.NOT_FOUND, errorMessage);
    }


    public T find(U id) {
        return getEntityManager().find(getEntityClass(), id);
    }

    public void toDelete(T t) {
        t.delete();
    }

    protected abstract String getDefaultOrderBy();

    public abstract PanacheQuery<T> getSearch(String orderBy) throws Exception;

    protected Sort sort(String orderBy) throws Exception {
        Sort sort = null;
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            if (orderBy != null && orderBy.contains(",")) {
                String[] orderByClause = orderBy.split(",");
                for (String pz : orderByClause) {
                    sort = single(sort, pz);
                }
                return sort;
            } else {
                return single(sort, orderBy);
            }
        }
        if (getDefaultOrderBy() != null && !getDefaultOrderBy().trim().isEmpty()) {
            if (getDefaultOrderBy().contains("asc"))
                return Sort.by(getDefaultOrderBy().replace("asc", "").trim()).ascending();
            if (getDefaultOrderBy().contains("desc"))
                return Sort.by(getDefaultOrderBy().replace("desc", "").trim()).ascending();
        }

        return null;
    }

    private Sort single(Sort sort, String orderBy) throws Exception {
        String[] orderByClause;
        if (orderBy.contains(":")) {
            orderByClause = orderBy.split(":");
        } else {
            orderByClause = orderBy.split(" ");
        }
        if (orderByClause.length > 1) {
            if (orderByClause[1].equals("asc")) {
                if (sort != null) {
                    return sort.and(orderByClause[0], Sort.Direction.Ascending);
                } else {
                    return Sort.by(orderByClause[0], Sort.Direction.Ascending);
                }

            } else if (orderByClause[1].equals("desc")) {
                if (sort != null) {
                    return sort.and(orderByClause[0], Sort.Direction.Descending);
                } else {
                    return Sort.by(orderByClause[0], Sort.Direction.Descending);
                }
            }
            throw new Exception("sort is not usable");
        } else {
            if (sort != null) {
                return sort.and(orderBy).descending();
            } else {
                return Sort.by(orderBy).ascending();
            }
        }
    }

}
