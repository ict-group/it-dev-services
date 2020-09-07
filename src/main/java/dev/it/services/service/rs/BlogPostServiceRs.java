package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.BlogPost;
import dev.it.services.service.S3Service;
import dev.it.services.service.TagEvent;
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
import java.util.*;

@Path(AppConstants.BLOGPOSTS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class BlogPostServiceRs extends RsRepositoryServiceV3<BlogPost, String> {

    @Inject
    S3Service s3Service;

    @Inject
    Event tagEvent;

    private String oldTags;

    public BlogPostServiceRs() {
        super(BlogPost.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " insert_date desc";
    }


    @Override
    public PanacheQuery<BlogPost> getSearch(String orderBy) throws Exception {
        PanacheQuery<BlogPost> search;
        Sort sort = sort(orderBy);
        if (sort != null) {
            search = BlogPost.find("select a from BlogPost a", sort);
        } else {
            search = BlogPost.find("select a from BlogPost a");
        }
        if (nn("obj.type")) {
            search
                    .filter("obj.type", Parameters.with("type", get("obj.type")));
        }
        if (nn("like.title")) {
            search
                    .filter("like.title", Parameters.with("title", likeParamToLowerCase("like.title")));
        }
        if (nn("like.tags")) {
            search
                    .filter("like.tags", Parameters.with("tags", likeParamToLowerCase("like.tags")));
        }
        if (nn("obj.author")) {
            search
                    .filter("obj.author", Parameters.with("author", get("obj.author")));
        }
        if (nn("obj.developer_uuid")) {
            search
                    .filter("obj.developer_uuid", Parameters.with("developer_uuid", get("obj.developer_uuid")));
        }
        return search;
    }

    @Override
    protected void postPersist(BlogPost blogPost) throws Exception {

        saveOrUpdateTagsForBlogpost(blogPost);
    }

    private void saveOrUpdateTagsForBlogpost(BlogPost blogPost) throws Exception {

        if(blogPost != null){

            if(blogPost.tags == null){

                logger.errorv("Blogpost should have it's corresponding tags.");
                //maybe throw an exception (NoTagsForBlogpostException) ? and catch it with a exception mapper ?
            }
            else{

                String[] tags = blogPost.tags.split(",");

                Arrays.stream(tags).forEach(tagName -> tagEvent.fireAsync(new TagEvent(tagName.toLowerCase().trim(), true)));
            }
        }
    }

    @Override
    protected BlogPost preUpdate(BlogPost blogPost) throws Exception {

        BlogPost existingBlogPost = BlogPost.findById(blogPost.uuid);

        oldTags = existingBlogPost.tags;

        return super.preUpdate(blogPost);
    }

    @Override
    protected void postUpdate(BlogPost blogPost) throws Exception {

        if(!oldTags.equals(blogPost.tags)) {

            String[] existingTags = oldTags.split(",");

            String[] newTags = blogPost.tags.split(",");

            //Find the similar elements that will not be touched
            Set<String> similar = new HashSet(Arrays.asList(existingTags));
            similar.retainAll(Arrays.asList(newTags));

            //Remove all tags removed from blogspot
            Set<String> toRemoveTags = new HashSet<>(Arrays.asList(existingTags));
            toRemoveTags.removeAll(similar);
            toRemoveTags.stream().forEach(tagName -> tagEvent.fireAsync(new TagEvent(tagName.toLowerCase().trim(), false)));

            //Add all tags added to blogspot
            Set<String> newTagsSet = new HashSet<>(Arrays.asList(newTags));
            newTagsSet.removeAll(similar);
            newTagsSet.stream().forEach(tagName -> tagEvent.fireAsync(new TagEvent(tagName.toLowerCase().trim(), true)));
        }
    }

}
