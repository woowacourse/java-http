package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK(200, "OK"),
    NOTFOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    HttpStatusCode(int status, String statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    private int status;
    private String statusCode;

    public static String getStatusCode(int status) {
        for (var statusCode : HttpStatusCode.values()) {
            if (statusCode.status == status) {
                return statusCode.statusCode;
            }
        }
        throw new IllegalArgumentException("Unrecognized status code.");
    }
}
