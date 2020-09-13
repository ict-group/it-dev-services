package dev.it.api.util;

import javax.persistence.EntityManager;

public class SlugUtils {

    public static String createSlug(String field) {
        if (field == null)
            return null;
        field = field.trim().replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("[\\s]", "-");
        return field.toLowerCase();
    }

    public static String makeUniqueSlug(String slug, Class entityClass, EntityManager entityManager) throws Exception {
        String slugged = createSlug(slug);
        String keyNotUsed = slugged;
        boolean found = false;
        int i = 0;
        while (!found) {
            if (entityManager.find(entityClass, keyNotUsed) != null) {
                i++;
                keyNotUsed = slugged + "-" + i;
            } else {
                return keyNotUsed;
            }
        }
        return "";
    }
}
