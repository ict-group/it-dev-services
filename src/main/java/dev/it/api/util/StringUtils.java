package dev.it.api.util;


import dev.it.api.management.AppConstants;

import java.text.MessageFormat;

public class StringUtils {

    private StringUtils() {
    }

    /**
     * Formatta una stringa prendendo il seguente formato come pattern: "benvenuto {0}! {1}"
     * @param pattern
     * @param args
     * @return
     */
    public static String formatMessage(String pattern, Object... args) {
        MessageFormat messageFormat = new MessageFormat(pattern);
        return messageFormat.format(args);
    }

    /**
     * Toglie i caratteri in blacklist (per evitare sql injection)
     * @param arg
     * @return
     */
    public static String replaceBlacklistCharacters(String arg) {
        return arg.replaceAll(AppConstants.BLACKLIST_CHARACTERS_REGEX, "");
    }

}
