package dev.it.services.service;

import dev.it.services.model.Tag;
import dev.it.services.model.pojo.TagEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.transaction.Transactional;

@ApplicationScoped
public class TagService {

    protected Logger logger = Logger.getLogger(getClass());

    @Transactional
    public void onEvent(@ObservesAsync TagEvent event) {
        if (event.isAdded()) {
            logger.error("ADDED");
            saveOrUpdateTag(event.getTag());
        } else {
            logger.error("REMOVED");
            removeOrUpdateTag(event.getTag());
        }
    }

    private void saveOrUpdateTag(String tagName) {
        Tag tag = Tag.find("name", tagName).firstResult();
        if (tag == null) {
            createTag(tagName);
        } else {
            updateTag(tag);
        }
    }

    private void createTag(String tagName) {
        logger.info("persist");
        Tag tag = new Tag(tagName);
        tag.persist();
        if (tag == null) {
            logger.error("Failed to create resource: " + tag);
        }
    }

    private void updateTag(Tag tag) {
        logger.info("update");
        tag.numberOf++;
    }

    private void removeOrUpdateTag(String tagName) {
        Tag tag = Tag.find("name", tagName).firstResult();
        tag.numberOf--;
        if (tag.numberOf == 0) {
            tag.delete();
        }
    }
}
