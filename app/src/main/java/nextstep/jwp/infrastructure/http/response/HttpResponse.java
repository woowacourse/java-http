package nextstep.jwp.infrastructure.http.response;


import nextstep.jwp.infrastructure.http.HttpHeaders;

public class HttpResponse {

    private final HttpStatusLine statusLine;
    private final HttpHeaders headers;
    private final String messageBody;

    public HttpResponse(final HttpStatusLine statusLine, final HttpHeaders headers, final String messageBody) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    @Override
    public String toString() {
        return String.join(System.lineSeparator(),
            statusLine.toString(),
            headers.toString(),
            "",
            messageBody
        );
    }
}
