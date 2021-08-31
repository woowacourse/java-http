package nextstep.jwp.infrastructure.http.response;


import java.util.Objects;
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

    public HttpResponse(final HttpStatusLine statusLine, final HttpHeaders headers) {
        this(statusLine, headers, "");
    }

    @Override
    public String toString() {
        return String.join("\r\n",
            statusLine.toString(),
            headers.toString(),
            "",
            messageBody
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpResponse that = (HttpResponse) o;
        return Objects.equals(statusLine, that.statusLine) && Objects.equals(headers, that.headers) && Objects
            .equals(messageBody, that.messageBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusLine, headers, messageBody);
    }
}
