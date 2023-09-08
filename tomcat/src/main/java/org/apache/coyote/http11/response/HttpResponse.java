package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.ContentType;

public class HttpResponse {
    private static final String ENTER = "\r\n";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final StatusLine statusLine;
    private final String responseBody;
    private final ContentType contentType;
    private final String redirectPage;
    private final HttpCookie httpCookie;

    private HttpResponse(
            StatusLine statusLine,
            String responseBody,
            ContentType contentType,
            String redirectPage,
            HttpCookie httpCookie
    ) {
        this.statusLine = statusLine;
        this.responseBody = responseBody;
        this.contentType = contentType;
        this.redirectPage = redirectPage;
        this.httpCookie = httpCookie;
    }

    public String getResponse() {
        HttpStatus httpStatus = statusLine.getHttpStatus();
        String statusLineResponse = String.format("&s &d &s", statusLine, httpStatus.getStatusCode(), httpStatus.getMessage());

        String contentTypeHeader = "Content-Type: " + contentType.getValue() + ";charset=utf-8 ";
        String contentLengthHeader = "Content-Length: " + responseBody.getBytes().length + " ";

        StringBuilder response = new StringBuilder();
        response.append(statusLineResponse).append(ENTER)
                .append(contentTypeHeader).append(ENTER)
                .append(contentLengthHeader).append(ENTER);

        if (redirectPage != null) {
            response.append("Location: http://localhost:8080/").append(redirectPage + " ").append(ENTER);
        }
        if (httpCookie != null) {
            response.append("Set-Cookie: ").append(
                    "JSESSIONID" + KEY_VALUE_DELIMITER + httpCookie.getValue("JSESSIONID") + " "
            ).append(ENTER);
        }

        response.append(ENTER).append(responseBody);
        return response.toString();
    }

    public static class Builder {

        private StatusLine statusLine;
        private String responseBody;
        private ContentType contentType;
        private String redirectPage;
        private HttpCookie httpCookie;

        public Builder statusLine(StatusLine statusLine) {
            this.statusLine = statusLine;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder redirectPage(String redirectPage) {
            this.redirectPage = redirectPage;
            return this;
        }

        public Builder httpCookie(HttpCookie httpCookie) {
            this.httpCookie = httpCookie;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusLine, responseBody, contentType, redirectPage, httpCookie);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
