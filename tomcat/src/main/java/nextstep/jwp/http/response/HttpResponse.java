package nextstep.jwp.http.response;

import java.util.StringJoiner;
import nextstep.jwp.http.HttpHeader;

public class HttpResponse {

    private static final String BLANK = " ";

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
