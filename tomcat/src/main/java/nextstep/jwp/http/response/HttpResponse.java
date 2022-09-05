package nextstep.jwp.http.response;

import java.util.StringJoiner;
import nextstep.jwp.http.HttpHeader;

public class HttpResponse {

    private static final String BLANK = " ";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";

    private String version = "HTTP/1.1";
    private StatusCode statusCode;
    private HttpHeader httpHeaders = new HttpHeader();
    private String responseBody;

    public HttpResponse() {
    }

    public void setStatus(final String status) {
        this.statusCode = StatusCode.valueOf(status);
    }

    public void setContentType(final String contentType) {
        this.httpHeaders.addHeader(HEADER_CONTENT_TYPE, contentType);
    }

    public void setContentLength(final int length) {
        this.httpHeaders.addHeader(HEADER_CONTENT_LENGTH, String.valueOf(length));
    }

    public void setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseTemplate() {
        String statusLineTemplate = createStatusLineTemplate();
        String headerTemplate = httpHeaders.createHeaderTemplate();
        String responseBodyTemplate = createResponseBodyTemplate();

        return statusLineTemplate + headerTemplate + responseBodyTemplate;
    }

    private String createStatusLineTemplate() {
        StringJoiner statusLineJoiner = new StringJoiner(BLANK);
        statusLineJoiner.add(version);
        statusLineJoiner.add(statusCode.getCode());
        statusLineJoiner.add(statusCode.getMessage());
        statusLineJoiner.add("\r\n");
        return statusLineJoiner.toString();
    }

    private String createResponseBodyTemplate() {
        return "\r\n"
                + "\r\n"
                + responseBody;
    }
}
