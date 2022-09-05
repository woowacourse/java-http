package org.apache.coyote.http11.mapping;

public class MappingResponse {

    private static final String NOT_FOUND_RESOURCE = "404.html";
    private static final String NOT_FOUND_STATUS_CODE = "Not Found";

    private final String resource;
    private final String statusCode;

    public MappingResponse(final String resource, final String statusCode) {
        this.resource = resource;
        this.statusCode = statusCode;
    }

    public static MappingResponse notFound() {
        return new MappingResponse(NOT_FOUND_RESOURCE, NOT_FOUND_STATUS_CODE);
    }

    public String getResource() {
        return resource;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
