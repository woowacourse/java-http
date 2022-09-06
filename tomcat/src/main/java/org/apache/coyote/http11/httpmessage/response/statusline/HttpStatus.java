package org.apache.coyote.http11.httpmessage.response.statusline;

public enum HttpStatus {
    OK("200", "OK "),
    REDIRECT("302", "");

    private final String statusCode;
    private final String reasonPhrase;

    HttpStatus(String statusCode, String status) {
        this.statusCode = statusCode;
        this.reasonPhrase = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
