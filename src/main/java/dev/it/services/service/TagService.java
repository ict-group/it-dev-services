package dev.it.services.service;

import dev.it.services.model.Tag;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.transaction.Transactional;

@ApplicationScoped
public class TagService {

    protected Logger logger = Logger.getLogger(getClass());

    @Transactional
    public void onEvent(@ObservesAsync TagEvent event) {

        if(event.isAdded()){

            logger.error("ADDED");
        }
        else {

            logger.error("REMOVED");
        }

//        saveOrUpdateTag(tagName);
    }

    private void saveOrUpdateTag(String tagName){

        Tag tag = Tag.find("name", tagName).firstResult();

        if(tag == null){

            createTag(tagName);
        }
        else {

            updateTag(tag);
        }
    }

    private void createTag(String tagName){

        logger.info("persist");

        Tag tag = new Tag(tagName);

        tag.persist();

        if (tag == null) {
            logger.error("Failed to create resource: " + tag);
        }
    }

    private void updateTag(Tag tag){

        logger.info("update");

        tag.numberOf++;
    }
}
