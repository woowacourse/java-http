package org.apache.catalina;

import org.apache.coyote.http11.HttpHeaders;

import static org.apache.coyote.http11.HttpHeaderType.ACCEPT;
import static org.apache.coyote.http11.HttpHeaderType.CONTENT_TYPE;

public class ResourceContentTypeResolver {

    public String getContentType(final HttpHeaders headers, final String resourceName) {
        final String contentTypeHeader = headers.getHeaderValue(CONTENT_TYPE);
        if (contentTypeHeader != null) {
            String firstContentType = contentTypeHeader.split(",")[0];
            // TODO: Check whether the content type is supported
            return firstContentType;
        }

        final String acceptHeader = headers.getHeaderValue(ACCEPT);
        if (acceptHeader != null) {
            String firstAcceptHeader = acceptHeader.split(",")[0];
            // TODO: Check whether the content type is supported
            return firstAcceptHeader;
        }

        return getFileExtension(resourceName);
    }

    private String getFileExtension(final String fileName) {
        final int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            final String fileExtension = fileName.substring(lastDotIndex + 1);
            // TODO: Check whether the content type is supported
            return fileExtension;
        }
        return null;
    }
}
