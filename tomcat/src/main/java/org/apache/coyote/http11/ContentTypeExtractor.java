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
        return !getExtension(httpRequest).isBlank();
    }

    private String getContentTypeByExtension(final HttpRequest httpRequest) {
        return "text/" + getExtension(httpRequest);
    }

    private String getExtension(final HttpRequest httpRequest) {
        if (httpRequest.getUriPath().contains(".")) {
            final int lastDotIndex = httpRequest.getUriPath().lastIndexOf('.');
            final String extension = httpRequest.getUriPath().substring(lastDotIndex + 1);
            return extension.equals("js") ? "javascript" : extension;
        }
        return "";
    }
}
