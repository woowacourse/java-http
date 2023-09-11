package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.HttpHeaderType.ACCEPT;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;

public class ResourceContentTypeResolver {

    public static String getContentType(final HttpHeaders headers, final String resourceName) {
        final String contentType = getFirstSupportedMediaType(headers.getHeaderValue(CONTENT_TYPE));
        if (contentType != null) {
            return contentType;
        }

        final String acceptHeader = getFirstSupportedMediaType(headers.getHeaderValue(ACCEPT));
        if (acceptHeader != null) {
            return acceptHeader;
        }

        return getFileExtension(resourceName);
    }

    private static String getFirstSupportedMediaType(String headerValue) {
        if (headerValue != null) {
            String[] mediaTypes = headerValue.split(",");
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
        return null;
    }
}
