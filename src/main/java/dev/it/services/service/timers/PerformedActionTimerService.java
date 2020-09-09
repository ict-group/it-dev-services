package dev.it.services.service.timers;

import dev.it.services.model.PerformedAction;
import dev.it.services.model.PerformedActionBlogPost;
import dev.it.services.model.pojo.ActionValue;
import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Singleton
public class PerformedActionTimerService{

    @Inject
    EntityManager entityManager;

    protected Logger logger = Logger.getLogger(getClass());

    @Scheduled(every="10m")
    @Transactional
    void proccessPerformedActions() {

        List<PerformedAction> performedActions = (List<PerformedAction>) entityManager.createNativeQuery("select * from performed_actions where working_date is null", PerformedAction.class)
                .getResultList();

        if(performedActions != null){

            performedActions.stream()
                    .forEach(performedAction -> elaboratePerformedAction(performedAction));

            logger.info("PerformedActions processed at : " + LocalDateTime.now());
        }
    }

    private void elaboratePerformedAction(PerformedAction performedAction) {

        PerformedActionBlogPost existsPerformedActionBlogPost = PerformedActionBlogPost.findById(performedAction.blogpost_uuid);

        if(existsPerformedActionBlogPost == null){

            createPerformedActionBlogPost(performedAction);
        }
        else{

            updatePerformedActionBlogPost(performedAction, existsPerformedActionBlogPost);
        }

        performedAction.working_date = LocalDate.now();
    }

    private void createPerformedActionBlogPost(PerformedAction performedAction) {

        PerformedActionBlogPost newPerformedActionBlogPost = new PerformedActionBlogPost();

        newPerformedActionBlogPost.uuid = performedAction.blogpost_uuid;
        newPerformedActionBlogPost.last_update = LocalDateTime.now();
        newPerformedActionBlogPost.actions.add(new ActionValue(performedAction.action));

        newPerformedActionBlogPost.persist();
    }

    private void updatePerformedActionBlogPost(PerformedAction performedAction, PerformedActionBlogPost existsPerformedActionBlogPost) {

        existsPerformedActionBlogPost.last_update = LocalDateTime.now();

        //Get the existingActionValue with the same action if exist and null if not
        ActionValue existingActionValue =  existsPerformedActionBlogPost.actions.stream()
                .filter(actionValue -> actionValue.action.equals(performedAction.action))
                .findAny()
                .orElse(null);

        if (existingActionValue == null){

            existsPerformedActionBlogPost.actions.add(new ActionValue(performedAction.action));
        }
        else{

            existingActionValue.numberOf++;
        }
    }
}
