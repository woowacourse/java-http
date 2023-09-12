package org.apache.coyote.http11.common;

import java.util.Optional;

public class ResourceContentTypeResolver {

    public static String getResourceContentType(final String acceptValue, final String resourceName) {
        return getFirstSupportedMediaType(acceptValue).orElse(getFileExtension(resourceName));
    }

    private static Optional<String> getFirstSupportedMediaType(final String acceptValue) {
        if (acceptValue != null) {
            String[] mediaTypes = acceptValue.split(",");
            for (String mediaTypeStr : mediaTypes) {
                final MediaType mediaType = MediaType.getMediaType(mediaTypeStr.trim());
                if (MediaType.isSupported(mediaType)) {
                    return Optional.of(mediaType.stringifyWithUtf());
                }
            }
        }
        return Optional.empty();
    }

    private static String getFileExtension(final String fileName) {
        final int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            final String fileExtension = fileName.substring(lastDotIndex + 1);
            final MediaType mediaType = MediaType.getMediaTypeByFileExtension(fileExtension);
            if (MediaType.isSupported(mediaType)) {
                return mediaType.stringifyWithUtf();
            }
        }
        return MediaType.ALL.stringifyWithUtf();
    }
}
