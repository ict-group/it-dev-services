package dev.it.services.service.rs;

import dev.it.api.service.RsRepositoryServiceV3;
import dev.it.services.management.AppConstants;
import dev.it.services.model.Attachment;
import dev.it.services.model.Developer;
import dev.it.services.model.pojo.FormData;
import dev.it.services.service.S3Service;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.runtime.JpaOperations;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Date;
import java.util.UUID;

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


    @GET
    @Path("/{uuid}/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam(value = "uuid") String uuid) throws Exception {
        final String logMessage = "@GET download: [{0}]";
        Attachment attachment = Attachment.findById(uuid);
        if (attachment == null) {
            return handleObjectNotFoundRequest(uuid);
        }
        logger.infov(logMessage, attachment);
        logger.info(MediaType.valueOf(attachment.mime_type));
        Response.ResponseBuilder response = Response.ok((StreamingOutput) output -> s3Service.downloadObject(attachment.uuid).writeTo(output));
        response.header("Content-Disposition", "attachment;filename=" + attachment.name);
        response.header("Content-Type", attachment.mime_type);
        return response.build();
    }


    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response upload(@MultipartForm FormData formData) {
        final String logMessage = "@POST addAttachment: [{0}]";
        Attachment attachment = new Attachment();
        try {
            attachment.uuid = UUID.randomUUID().toString();
            performDocumentUploading(attachment, formData, logMessage);
            JpaOperations.persist(attachment);
            if (attachment == null || attachment.uuid == null) {
                logger.error("Failed to create resource: " + attachment);
                return jsonErrorMessageResponse(attachment);
            } else {
                return Response.status(Response.Status.OK).entity(attachment).build();
            }
        } catch (Exception e) {
            logger.errorv(e, logMessage);
            return jsonErrorMessageResponse(attachment);
        }

    }

    private void performDocumentUploading(Attachment attachment, FormData formData, String logMessage) throws Exception {
        attachment.name = formData.fileName;
        attachment.mime_type = formData.mimeType;
        attachment.external_type = formData.external_type;
        attachment.external_uuid = formData.external_uuid;
        attachment.creation_date = new Date();
        logger.infov(logMessage, attachment);
        PutObjectResponse result = s3Service.uploadObject(formData, attachment.uuid);
        attachment.s3name = attachment.uuid;
    }
}