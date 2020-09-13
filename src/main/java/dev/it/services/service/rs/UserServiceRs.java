package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.BlogPost;
import dev.it.services.model.User;
import dev.it.services.service.S3Service;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(AppConstants.USERS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class UserServiceRs extends RsRepositoryServiceV3<User, String> {


    public UserServiceRs() {
        super(User.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " username desc";
    }


    @Override
    public PanacheQuery<User> getSearch(String orderBy) throws Exception {
        PanacheQuery<User> search;
        Sort sort = sort(orderBy);
        if (sort != null) {
            search = User.find("select a from User a", sort);
        } else {
            search = User.find("select a from User a");
        }
        if (nn("obj.username")) {
            search
                    .filter("obj.username", Parameters.with("username", get("obj.username")));
        }
        if (nn("like.username")) {
            search
                    .filter("like.username", Parameters.with("username", likeParamToLowerCase("like.username")));
        }
        if (nn("like.roles")) {
            search
                    .filter("like.roles", Parameters.with("roles", likeParamToLowerCase("like.roles")));
        }
        return search;
    }


    @Override
    protected void prePersist(User user) throws Exception {
        //Check if the user tries to create a new User with the same username
        if (user.username != null) {
            User existingUser = User.findById(user.username);
            if (existingUser != null) {
                logger.error("User with username : " + user.username + " already exists!");
                throw new IllegalArgumentException("User with username :" + user.username + " already exists !");
            }
        } else {
            throw new IllegalArgumentException("User with username null!");
        }
    }
}
