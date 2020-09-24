package dev.it.api.service.v4;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

import static dev.it.api.service.RsResponseService.jsonErrorMessageResponse;
import static dev.it.api.service.RsResponseService.jsonMessageResponse;

@ApplicationScoped
public class UpdateService<T extends PanacheEntityBase, U> {
    private static final long serialVersionUID = 1L;
    protected Logger logger = Logger.getLogger(getClass());

    @Inject
    EntityManager entityManager;

    public Response update(U id, T object) {
        return this.update(id, () -> object, () -> null);
    }

    public Response update(U id, T object, Callable<T> postUpdate) {
        return this.update(id, () -> object, postUpdate);
    }

    public Response update(@PathParam("id") U id, Callable<T> preUpdateFn, Callable<T> postUpdate) {
        logger.info("update:" + id);
        T object = null;

        try {
            object = preUpdateFn.call();
        } catch (Exception e) {
            logger.errorv(e, "update:" + id);
            return jsonMessageResponse(Response.Status.BAD_REQUEST, e);
        }
        try {
            entityManager.merge(object);
            return Response.status(Response.Status.OK).entity(object).build();
        } catch (Exception e) {
            logger.errorv(e, "update:" + id);
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postUpdate.call();
            } catch (Exception e) {
                logger.errorv(e, "update:" + id);
            }
        }
    }
}
