package org.apache.coyote.http11;

public class ContentTypeExtractor {

    public String extract(final HttpRequest httpRequest) {
        if (httpRequest.hasHeader("Accept")) {
            final String acceptHeaderValue = httpRequest.getHeader("Accept");
            return getContentTypeByAcceptHeader(acceptHeaderValue);
        }

        if (hasExtension(httpRequest)) {
            return getContentTypeByExtension(httpRequest);
        }

        return "text/html";
    }

    private String getContentTypeByAcceptHeader(String values) {
        return values.trim().split(",")[0];
    }

    private boolean hasExtension(final HttpRequest httpRequest) {
        return httpRequest.getUri().contains(".");
    }

    private String getContentTypeByExtension(final HttpRequest httpRequest) {
        return "text/" + getExtension(httpRequest);
    }

    private String getExtension(final HttpRequest httpRequest) {
        final int lastDotIndex = httpRequest.getUri().lastIndexOf('.');
        final String extension = httpRequest.getUri().substring(lastDotIndex + 1);
        return extension.equals("js") ? "javascript" : extension;
    }
}
