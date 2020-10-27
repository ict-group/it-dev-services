package dev.it.api.service.v4;

import io.netty.util.internal.StringUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.Callable;

import static dev.it.api.service.RsResponseService.jsonErrorMessageResponse;
import static dev.it.api.service.RsResponseService.jsonMessageResponse;

@ApplicationScoped
public class CreateService<T extends PanacheEntityBase, U> {
    private static final long serialVersionUID = 1L;

    protected Logger logger = Logger.getLogger(getClass());

    @Inject
    EntityManager entityManager;

    public Response persist(T object) {
        return persist(object, true);
    }

    public Response persist(T object, boolean autoUUID) {
        if (autoUUID) {
            return persist(() -> this.generateUUID(object), this.nothing(), this.nothing());
        } else {
            return persist(object, this.nothing(), this.nothing());
        }
    }

    public Response persist(T object, String assignUUID, String uuidField) {
        if (!StringUtil.isNullOrEmpty(assignUUID)) {
            return persist(() -> this.assingUUIDKey(object, uuidField, assignUUID), this.nothing(), this.nothing());
        } else {
            return persist(object, this.nothing(), this.nothing());
        }
    }

    public Callable<Void> nothing() {
        return () -> null;
    }

    public Response persist(Callable<T> uuidFn,
                            Callable<Void> prePersistFn,
                            Callable<Void> postPersistFn) {
        logger.info("auto uuid generation");
        try {
            T newObject = uuidFn.call();
            return this.persist(newObject, prePersistFn, postPersistFn);
        } catch (Exception e) {
            logger.errorv(e, "uuid invalid");
            return jsonMessageResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    public Response persist(T object, Callable<Void> prePersistFn, Callable<Void> postPersistFn) {
        logger.info("persist");
        try {
            prePersistFn.call();
        } catch (Exception e) {
            logger.errorv(e, "persist");
            return jsonMessageResponse(Response.Status.BAD_REQUEST, e);
        }
        try {
            entityManager.persist(object);
            if (object == null) {
                logger.error("Failed to create resource: " + object);
                return jsonErrorMessageResponse(object);
            } else {
                return Response.status(Response.Status.OK).entity(object).build();
            }
        } catch (Exception e) {
            logger.errorv(e, "persist");
            return jsonErrorMessageResponse(object);
        } finally {
            try {
                postPersistFn.call();
            } catch (Exception e) {
                logger.errorv(e, "persist");
            }
        }
    }

    public T generateUUID(T object) throws NoSuchFieldException, IllegalAccessException {
        String uuid = UUID.randomUUID().toString();
        return assingUUIDKey(object, "uuid", uuid);
    }

    private T assingUUIDKey(T object, String key, String value) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = null;
        try {

            declaredField = object.getClass().getDeclaredField(key);
            boolean accessible = declaredField.trySetAccessible();
            declaredField.setAccessible(true);
            declaredField.set(object, value);
            declaredField.setAccessible(accessible);

            return object;
        } catch (NoSuchFieldException
                | SecurityException
                | IllegalArgumentException
                | IllegalAccessException e) {
            throw e;
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
