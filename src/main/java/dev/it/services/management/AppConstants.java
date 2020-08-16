package dev.it.services.management;

public final class AppConstants {

    public static final String MULTIPLE_VALUES_SEPARATOR = ",|;";
    public static final String COMMA_SEPARATOR = ",";
    public static final String ORDER_BY_CLAUSE_SEPARATOR = ":";
    public static final String BLACKLIST_CHARACTERS_REGEX = "([%'])";

    public static final String API_PATH = "/api/v1";
    public static final String ATTACHMENTS_PATH = API_PATH + "/attachments";
    public static final String BLOGPOSTS_PATH = API_PATH + "/blogposts";
    public static final String DEVELOPERS_PATH = API_PATH + "/developers";
    public static final String PROJECTS_PATH = API_PATH + "/projects";
    public static final String USERS_PATH = API_PATH + "/users";

    // COSTANTI PER LEGGERE FileUploadRest
    public static final String UPLOAD_FILE = "file";
    public static final String UPLOAD_FOLDER = "folder";
    public static final String UPLOAD_DOCUMENT_UUID = "document_uuid";
    public static final String UPLOAD_TAGS = "tags";
    public static final String UPLOAD_DOCUMENT_GROUP = "group";

    // Ruoli Utente
    public static final String ADMIN_ROLE = "admin";


    public static final String TRASH_FOLDER = "Trash";
}
