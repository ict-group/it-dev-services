package dev.it.api.service.v4;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;

import static dev.it.api.service.RsResponseService.jsonErrorMessageResponse;
import static dev.it.api.service.RsResponseService.jsonMessageResponse;

@ApplicationScoped
public class FetchService<T extends PanacheEntityBase, U> {
    private static final long serialVersionUID = 1L;
    protected Logger logger = Logger.getLogger(getClass());

    @Inject
    EntityManager entityManager;

    public Response fetch(U id, Class<T> tClass) {
        return fetch(id, object -> null, tClass);
    }

    public Response fetch(U id, PostFetch postFetch, Class<T> tClass) {
        logger.info("fetch: " + id);

        try {
            T t = find(id, tClass);
            if (t == null) {
                return handleObjectNotFoundRequest(id, tClass);
            } else {
                try {
                    postFetch.call(t);
                } catch (Exception e) {
                    logger.errorv(e, "fetch: " + id);
                }
                return Response.status(Response.Status.OK).entity(t).build();
            }
        } catch (NoResultException e) {
            logger.errorv(e, "fetch: " + id);
            return jsonMessageResponse(Response.Status.NOT_FOUND, id);
        } catch (Exception e) {
            logger.errorv(e, "fetch: " + id);
            return jsonErrorMessageResponse(e);
        }
    }


    /**
     * Gestisce la risposta a seguito di un oggetto non trovato
     *
     * @param id
     * @return
     */
    protected Response handleObjectNotFoundRequest(U id, Class<T> tClass) {
        String errorMessage = MessageFormat.format("Object [{0}] with id [{1}] not found",
                tClass.getCanonicalName(), id);
        return jsonMessageResponse(Response.Status.NOT_FOUND, errorMessage);
    }

    public T find(U id, Class<T> tClass) {
        return entityManager.find(tClass, id);
    }

}
