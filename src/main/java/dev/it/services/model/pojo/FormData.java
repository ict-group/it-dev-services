package dev.it.services.model.pojo;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

public class FormData {

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream data;

    @FormParam("name")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;

    @FormParam("mime_type")
    @PartType(MediaType.TEXT_PLAIN)
    public String mimeType;

    @FormParam("document_uuid")
    @PartType(MediaType.TEXT_PLAIN)
    public String document_uuid;

    @FormParam("tags")
    @PartType(MediaType.TEXT_PLAIN)
    public String tags;

    @FormParam("group")
    @PartType(MediaType.TEXT_PLAIN)
    public String group;

    @FormParam("folder")
    @PartType(MediaType.TEXT_PLAIN)
    public String folder;
}
