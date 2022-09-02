package nextstep.jwp.http.response;

import nextstep.jwp.http.HttpHeader;

public class HttpResponse {

    private static final String BLANK = " ";
    private static final String SEPARATOR = "\r\n";

    private final String version;
    private final StatusCode statusCode;
    private final HttpHeader httpHeaders;
    private final String responseBody;

    public HttpResponse(final String version, final String status, final HttpHeader httpHeaders,
                        final String responseBody) {
        this.version = version;
        this.statusCode = StatusCode.valueOf(status);
        this.httpHeaders = httpHeaders;
        this.responseBody = responseBody;
    }

    public String getResponseTemplate() {
        String statusLineTemplate = createStatusLineTemplate();
        String headerTemplate = httpHeaders.createHeaderTemplate();
        String responseBodyTemplate = createResponseBodyTemplate();

        return statusLineTemplate + headerTemplate + responseBodyTemplate;
    }

    private String createStatusLineTemplate() {
        return version + BLANK + statusCode.getCode() + BLANK + statusCode.getMessage() + BLANK + SEPARATOR;
    }

    private String createResponseBodyTemplate() {
        return SEPARATOR + SEPARATOR + responseBody;
    }
}
