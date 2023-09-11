package org.apache.coyote.http11.common;

public class ResourceContentTypeResolver {

    public static String getResourceContentType(final String acceptValue, final String resourceName) {
        final String acceptHeader = getFirstSupportedMediaType(acceptValue);
        if (acceptHeader != null) {
            return acceptHeader;
        }
        return getFileExtension(resourceName);
    }

    private static String getFirstSupportedMediaType(final String acceptValue) {
        if (acceptValue != null) {
            String[] mediaTypes = acceptValue.split(",");
            for (String mediaTypeStr : mediaTypes) {
                final MediaType mediaType = MediaType.getMediaType(mediaTypeStr.trim());
                if (MediaType.isSupported(mediaType)) {
                    return mediaType.stringifyWithUtf();
                }
            }
        }
        return null;
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
