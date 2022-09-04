package org.apache.coyote.http11;

import java.util.Optional;

public class ContentTypeExtractor {

    public ContentType extract(final HttpRequest request) {
        if (request.hasHeader("Accept")) {
            final String acceptHeaderValue = request.getHeader("Accept");
            return getContentTypeByAcceptHeader(acceptHeaderValue);
        }

        return getExtension(request).orElse(ContentType.TEXT_HTML);
    }

    private ContentType getContentTypeByAcceptHeader(String values) {
        final String value = values.trim().split(",")[0];
        return ContentType.getByAcceptHeader(value)
                .orElse(ContentType.TEXT_HTML);
    }

    private Optional<ContentType> getExtension(final HttpRequest httpRequest) {
        if (httpRequest.getUriPath().contains(".")) {
            final int lastDotIndex = httpRequest.getUriPath().lastIndexOf('.');
            final String extension = httpRequest.getUriPath().substring(lastDotIndex + 1);
            return ContentType.getByExtension(extension);
        }
        return Optional.empty();
    }
}
