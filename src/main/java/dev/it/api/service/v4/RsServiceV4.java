package dev.it.api.service.v4;

import io.netty.util.internal.StringUtil;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Class for manage the routes to expose by default response http 404.
 * <p>
 *
 * @author Jose Medina.
 * @version 1.0
 */
public interface RsServiceV4<T, U> {

    @POST
    default Response persistI(T object) throws Exception {
        return defaultResponse(null);
    }

    @GET
    @Path("/{id}")
    default Response fetchI(@PathParam("id") U id) {
        return defaultResponse(id);
    }

    @PUT
    @Path("/{id}")
    default Response updateI(@PathParam("id") U id, T object) {
        return defaultResponse(id);
    }

    @DELETE
    @Path("/{id}")
    default Response deleteI(@PathParam("id") U id) {
        return defaultResponse(id);
    }

    @GET
    @Path("/{id}/exist")
    default Response existI(@PathParam("id") U id) {
        return defaultResponse(id);
    }

    @GET
    @Path("/listSize")
    default Response getListSizeI(@Context UriInfo ui) {
        return defaultResponse(null);
    }

    @GET
    default Response getListI(
            @DefaultValue("0") @QueryParam("startRow") Integer startRow,
            @DefaultValue("10") @QueryParam("pageSize") Integer pageSize,
            @QueryParam("orderBy") String orderBy, @Context UriInfo ui) {
        return defaultResponse(null);
    }

    /**
     * Default respot when the path is not found.
     *
     * @param id
     * @return
     */
    default Response defaultResponse(U id) {
        if (id instanceof String && !StringUtil.isNullOrEmpty((String) id)) {
            return Response.status(Response.Status.NOT_FOUND).entity(new JsonObject().put("id", id)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


}