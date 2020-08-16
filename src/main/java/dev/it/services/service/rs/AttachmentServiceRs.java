package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Attachment;
import dev.it.services.model.Developer;
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

@Path(AppConstants.ATTACHMENTS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class AttachmentServiceRs extends RsRepositoryServiceV3<Attachment, String> {

    @Inject
    S3Service s3Service;

    public AttachmentServiceRs() {
        super(Attachment.class);
    }


    @Override
    protected String getDefaultOrderBy() {
        return " name asc";
    }


    @Override
    public PanacheQuery<Attachment> getSearch(String orderBy) throws Exception {
        PanacheQuery<Attachment> search;
        Sort sort = sort(orderBy);
        if (sort != null) {
            search = Attachment.find("select a from Attachment a", sort);
        } else {
            search = Attachment.find("select a from Attachment a");
        }
        if (nn("obj.table_name")) {
            search
                    .filter("obj.table_name", Parameters.with("table_name", get("obj.table_name")));
        }
        if (nn("obj.table_key")) {
            search
                    .filter("obj.table_key", Parameters.with("table_key", get("obj.table_key")));
        }
        return search;
    }

}
