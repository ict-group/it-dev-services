package dev.it.api.util;

public class TableKeyUtils {

    public static String createSlug(String field) {
        if (field == null)
            return null;
        field = field.trim().replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("[\\s]", "-");
        return field.toLowerCase();
    }
}
