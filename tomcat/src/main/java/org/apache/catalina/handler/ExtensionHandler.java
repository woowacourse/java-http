package org.apache.catalina.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExtensionHandler.class);

    public static boolean hasFileExtension(final String uriString) {
        String extension = extractExtension(uriString);
        return FileExtension.isSupported(extension);
    }

    public static FileExtension getFileExtension(final String uriString) {
        String extension = extractExtension(uriString);
        if (extension == null || extension.isEmpty()) {
            return FileExtension.HTML;
        }
        return FileExtension.fromExtension(extension);
    }

    private static String extractExtension(final String uriString) {
        if (uriString == null || uriString.trim().isEmpty()) {
            log.debug("path is null or empty");
            return null;
        }

        try {
            String fileName = uriString.substring(uriString.lastIndexOf('/') + 1);

            if (fileName.isEmpty()) {
                return null;
            }

            int lastDotIndex = fileName.lastIndexOf('.');

            if (lastDotIndex <= 0 || lastDotIndex == fileName.length() - 1) {
                return null;
            }

            return fileName.substring(lastDotIndex).toLowerCase();

        } catch (Exception e) {
            log.debug("Error extracting extension from URI: {} - {}", uriString, e.getMessage());
            return null;
        }
    }
}
